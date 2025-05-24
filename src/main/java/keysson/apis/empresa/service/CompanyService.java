package keysson.apis.empresa.service;

import keysson.apis.empresa.dto.request.RequestRegisterCompany;
import keysson.apis.empresa.dto.response.ResponseEmpresa;
import keysson.apis.empresa.exception.BusinessRuleException;
import keysson.apis.empresa.exception.enums.ErrorCode;
import keysson.apis.empresa.repository.CompanyRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public ResponseEmpresa registerCompany(RequestRegisterCompany requestRegisterCompany) throws BusinessRuleException {

        if (companyRepository.existsByCnpj(requestRegisterCompany.getCnpj())) {
            throw new BusinessRuleException(ErrorCode.CNPJ_JA_CADASTRADO);
        }

        int numeroConta = gerarNumeroContaUnico();

        String encodedPassword = passwordEncoder.encode(requestRegisterCompany.getPassword());

        String consumerId = UUID.randomUUID().toString();

        companyRepository.save(
                requestRegisterCompany.getName(),
                requestRegisterCompany.getEmail(),
                requestRegisterCompany.getCnpj(),
                numeroConta,
                encodedPassword,
                requestRegisterCompany.getUsername(),
                1
        );

        return ResponseEmpresa.builder()
                .nome(requestRegisterCompany.getName())
                .conta(numeroConta)
                .build();
    }

    private int gerarNumeroContaUnico() {
        Random random = new Random();
        int numero;

        do {
            numero = 100000 + random.nextInt(900000); // número de 6 dígitos
        } while (companyRepository.existsByNumeroConta(numero));

        return numero;
    }
}
