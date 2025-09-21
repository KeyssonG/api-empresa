package keysson.apis.empresa.service;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import keysson.apis.empresa.Utils.JwtUtil;
import keysson.apis.empresa.dto.RegisteredCompanyEvent;
import keysson.apis.empresa.dto.request.RequestRegisterCompany;
import keysson.apis.empresa.dto.response.CompanyRegistrationResult;
import keysson.apis.empresa.dto.response.CompanyResponse;
import keysson.apis.empresa.dto.response.EmployeeResponse;
import keysson.apis.empresa.dto.response.UserCountResponse;
import keysson.apis.empresa.exception.BusinessRuleException;
import keysson.apis.empresa.exception.enums.ErrorCode;
import keysson.apis.empresa.repository.CompanyRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
    private static final String RABBITMQ_CB = "rabbitmqCB";

    private final CompanyRepository companyRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RabbitService rabbitService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private HttpServletRequest httpRequest;

    @Autowired
    private JwtUtil jwtUtil;


    public CompanyService(CompanyRepository companyRepository, RabbitService rabbitService, RabbitTemplate rabbitTemplate) {
        this.companyRepository = companyRepository;
        this.rabbitService = rabbitService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.rabbitTemplate = rabbitTemplate;
    }

    @CircuitBreaker(name = RABBITMQ_CB, fallbackMethod = "fallbackSendToRabbitMQ")
    private void sendToRabbitMQ(String queue, Object message, RegisteredCompanyEvent event) throws SQLException {
        rabbitTemplate.convertAndSend(queue, message);
        rabbitService.saveMessagesInBank(event, 1);
        logger.info("Mensagem enviada para a fila RabbitMQ com sucesso.");
    }

    private void fallbackSendToRabbitMQ(String queue, Object message, RegisteredCompanyEvent event, Throwable t) throws SQLException {
        logger.error("Circuit breaker ativado ao enviar mensagem ao RabbitMQ: {}", t.getMessage());
        rabbitService.saveMessagesInBank(event, 0);
    }

    public CompanyResponse registerCompany(RequestRegisterCompany requestRegisterCompany) throws BusinessRuleException, SQLException {
        logger.info("Iniciando registro de empresa com CNPJ: {}", requestRegisterCompany.getCnpj());

        if (companyRepository.existsByCnpj(requestRegisterCompany.getCnpj())) {
            logger.warn("CNPJ já cadastrado: {}", requestRegisterCompany.getCnpj());
            throw new BusinessRuleException(ErrorCode.CNPJ_JA_CADASTRADO);
        }

        int numeroConta = gerarNumeroContaUnico();
        logger.debug("Número de conta gerado: {}", numeroConta);

        String encodedPassword = passwordEncoder.encode(requestRegisterCompany.getPassword());
        logger.debug("Senha codificada para o registro.");

        String consumerId = UUID.randomUUID().toString();
        logger.debug("Consumer ID gerado: {}", consumerId);

        CompanyRegistrationResult result;


        try {
            result = companyRepository.save(
                    requestRegisterCompany.getName(),
                    requestRegisterCompany.getEmail(),
                    requestRegisterCompany.getCnpj(),
                    numeroConta,
                    encodedPassword,
                    requestRegisterCompany.getUsername(),
                    1
            );
        } catch (Exception e) {
            logger.error("Erro ao cadastrar empresa: {}", e.getMessage());
            throw new BusinessRuleException(ErrorCode.ERRO_CADASTRAR);
        }

        if (result.getResultCode() == 0) {
            RegisteredCompanyEvent event = new RegisteredCompanyEvent(
                    result.getIdEmpresa(),
                    requestRegisterCompany.getName(),
                    requestRegisterCompany.getEmail(),
                    requestRegisterCompany.getCnpj(),
                    requestRegisterCompany.getUsername()
            );
            try {
                sendToRabbitMQ("empresa.fila", event, event);
            } catch (Exception ex) {
                logger.error("Erro ao enviar mensagem ao RabbitMQ: {}", ex.getMessage());
                // fallback já salva como pendente
                throw new RuntimeException("Erro ao enviar mensagem ao RabbitMQ: " + ex.getMessage());
            }
        } else if (result.getResultCode() == 1) {
            logger.error("Erro ao cadastrar empresa, código de resultado: {}", result.getResultCode());
            throw new BusinessRuleException(ErrorCode.ERRO_CADASTRAR);
        }

        logger.info("Registro de empresa concluído com sucesso para o CNPJ: {}", requestRegisterCompany.getCnpj());

        return CompanyResponse.builder()
                .nome(requestRegisterCompany.getName())
                .conta(numeroConta)
                .build();
    }

    private int gerarNumeroContaUnico() {
        Random random = new Random();
        int numero;

        do {
            numero = 100000 + random.nextInt(900000);
        } while (companyRepository.existsByNumeroConta(numero));

        return numero;
    }

    public UserCountResponse searchUsersByDate(Date startDate, Date endDate) throws BusinessRuleException, SQLException {

        UserCountResponse response = companyRepository.findUsersByDate(startDate, endDate);
        if (response == null) {
            throw new BusinessRuleException(ErrorCode.USUARIOS_NAO_ENCONTRADOS);
        }

        return response;
    }

    public List<EmployeeResponse> searchEmployeesByDepartmentAndDate(String departamento, Date startDate, Date endDate) throws BusinessRuleException, SQLException {

        String token = (String) httpRequest.getAttribute("CleanJwt");

        Integer idEmpresa = jwtUtil.extractCompanyId(token);
        if (idEmpresa == null) {
            throw new IllegalArgumentException("ID da empresa não encontrado no token.");
        }

        List<EmployeeResponse> response = companyRepository.findEmployeesByDepartmentAndDate(departamento, startDate, endDate, idEmpresa);
        if (response == null) {
            throw new BusinessRuleException(ErrorCode.FUNCIONARIOS_NAO_ENCONTRADOS);
        }
        return response;
    }

}
