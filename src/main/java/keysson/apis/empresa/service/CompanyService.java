package keysson.apis.empresa.service;

import jakarta.annotation.PostConstruct;
import keysson.apis.empresa.dto.EmpresaCadastradaEvent;
import keysson.apis.empresa.dto.request.RequestRegisterCompany;
import keysson.apis.empresa.dto.response.EmpresaRegistroResultado;
import keysson.apis.empresa.dto.response.ResponseEmpresa;
import keysson.apis.empresa.exception.BusinessRuleException;
import keysson.apis.empresa.exception.enums.ErrorCode;
import keysson.apis.empresa.repository.CompanyRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public CompanyService(CompanyRepository companyRepository, RabbitTemplate rabbitTemplate) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.rabbitTemplate = rabbitTemplate;
    }

    public ResponseEmpresa registerCompany(RequestRegisterCompany requestRegisterCompany) throws BusinessRuleException {

        if (companyRepository.existsByCnpj(requestRegisterCompany.getCnpj())) {
            throw new BusinessRuleException(ErrorCode.CNPJ_JA_CADASTRADO);
        }

        int numeroConta = gerarNumeroContaUnico();

        String encodedPassword = passwordEncoder.encode(requestRegisterCompany.getPassword());

        String consumerId = UUID.randomUUID().toString();

        EmpresaRegistroResultado resultado =companyRepository.save(
                requestRegisterCompany.getName(),
                requestRegisterCompany.getEmail(),
                requestRegisterCompany.getCnpj(),
                numeroConta,
                encodedPassword,
                requestRegisterCompany.getUsername(),
                1
        );

        if (resultado.getResultCode() == 0) {
            EmpresaCadastradaEvent event = new EmpresaCadastradaEvent(
                    resultado.getIdEmpresa(),
                    requestRegisterCompany.getName(),
                    requestRegisterCompany.getEmail(),
                    requestRegisterCompany.getCnpj(),
                    requestRegisterCompany.getUsername()
            );
            rabbitTemplate.convertAndSend("empresa.fila", event);
        } else if (resultado.getResultCode() == 1) {
            throw new BusinessRuleException(ErrorCode.ERRO_CADASTRAR);
        }

        return ResponseEmpresa.builder()
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
}
