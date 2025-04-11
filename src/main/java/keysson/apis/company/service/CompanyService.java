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

        Company company = Company.builder()
                .name(requestRegisterCompany.getName())
                .cnpj(requestRegisterCompany.getCnpj())
                .password(passwordEncoder.encode(requestRegisterCompany.getPassword()))
                .status(1)
                .build();

        entityManager.persist(company);
        return company;
    }
}
