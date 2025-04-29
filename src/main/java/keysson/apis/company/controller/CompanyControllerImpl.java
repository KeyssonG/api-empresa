package keysson.apis.company.controller;

import keysson.apis.company.dto.request.RequestRegisterCompany;
import keysson.apis.company.dto.response.ResponseEmpresa;
import keysson.apis.company.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyControllerImpl implements CompanyController{

    private final CompanyService companyService;

    @Autowired
    public CompanyControllerImpl(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public ResponseEmpresa register(@RequestBody RequestRegisterCompany requestRegisterCompany) {
           return companyService.registerCompany(requestRegisterCompany);
    }
}
