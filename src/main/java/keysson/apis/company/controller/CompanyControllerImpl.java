package keysson.apis.company.controller;

import keysson.apis.company.dto.CompanyDTO;
import keysson.apis.company.dto.request.RequestRegisterCompany;
import keysson.apis.company.entity.Company;
import keysson.apis.company.exception.BusinessRuleException;
import keysson.apis.company.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyControllerImpl implements ICompanyController{

    private final CompanyService companyService;

    @Autowired
    public CompanyControllerImpl(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public void register(@RequestBody RequestRegisterCompany requestRegisterCompany) throws BusinessRuleException {
        Company createdCompany = companyService.registerCompany(requestRegisterCompany);
    }
}
