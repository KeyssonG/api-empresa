package keysson.apis.empresa.controller;

import keysson.apis.empresa.dto.request.RequestRegisterCompany;
import keysson.apis.empresa.dto.response.ResponseEmpresa;
import keysson.apis.empresa.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class CompanyControllerImpl implements CompanyController{

    private final CompanyService companyService;

    @Autowired
    public CompanyControllerImpl(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public ResponseEmpresa register(@RequestBody RequestRegisterCompany requestRegisterCompany) throws SQLException {
           return companyService.registerCompany(requestRegisterCompany);
    }
}
