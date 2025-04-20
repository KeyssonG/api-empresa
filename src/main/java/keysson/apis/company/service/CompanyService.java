package keysson.apis.company.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import keysson.apis.company.dto.request.RequestRegisterCompany;
import keysson.apis.company.entity.Company;
import keysson.apis.company.exception.BusinessRuleException;
import keysson.apis.company.exception.enums.ErrorCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Random;
import java.util.UUID;

import static keysson.apis.company.exception.enums.ErrorCode.CNPJ_JA_CADASTRADO;

@Service
public class CompanyService {

    @PersistenceContext
    private EntityManager entityManager;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Company registerCompany(RequestRegisterCompany requestRegisterCompany) throws BusinessRuleException {

        var existing = entityManager
                .createQuery("SELECT c FROM Company c WHERE c.cnpj = :cnpj", Company.class)
                .setParameter("cnpj", requestRegisterCompany.getCnpj())
                .getResultStream()
                .findFirst();

        if (existing.isPresent()) {
            throw new BusinessRuleException(ErrorCode.CNPJ_JA_CADASTRADO);
        }

        int numeroConta = gerarNumeroContaUnico();

        Company company = Company.builder()
                .name(requestRegisterCompany.getName())
                .cnpj(requestRegisterCompany.getCnpj())
                .numero_conta(numeroConta)
                .password(passwordEncoder.encode(requestRegisterCompany.getPassword()))
                .status(1)
                .build();

        entityManager.persist(company);
        return company;
    }

    private int gerarNumeroContaUnico() {
        Random random = new Random();
        int numero;

        do {
            numero = 100000 + random.nextInt(900000);
        } while (numeroContaJaExiste(numero));

        return numero;
    }

    private boolean numeroContaJaExiste(int numero) {
        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM Company c WHERE c.numero_conta = :numero", Long.class)
                .setParameter("numero", numero)
                .getSingleResult();
        return count > 0;
    }
}
