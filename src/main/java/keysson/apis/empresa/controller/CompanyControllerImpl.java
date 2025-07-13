package keysson.apis.empresa.controller;

import keysson.apis.empresa.config.FormatDate;
import keysson.apis.empresa.dto.request.RequestRegisterCompany;
import keysson.apis.empresa.dto.response.ResponseEmpresa;
import keysson.apis.empresa.dto.response.ResponseQuantidadeUsers;
import keysson.apis.empresa.exception.BusinessRuleException;
import keysson.apis.empresa.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Date;

@RestController
public class CompanyControllerImpl implements CompanyController{

    private final CompanyService companyService;

    @Autowired
    public CompanyControllerImpl(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    @PostMapping("/register")
    public ResponseEmpresa register(@RequestBody RequestRegisterCompany requestRegisterCompany) throws SQLException {
           return companyService.registerCompany(requestRegisterCompany);
    }

    @Override
    @GetMapping("/users")
    public ResponseQuantidadeUsers searchusers(String token,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) throws BusinessRuleException, SQLException {
        if (startDate == null || startDate.isEmpty()) {
            startDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        if (endDate == null || endDate.isEmpty()) {
            endDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        java.util.Date inicioFormatado = FormatDate.formatDate(startDate);
        java.util.Date fimFormatado = FormatDate.formatDate(endDate);
        return companyService.searchUsersByDate(inicioFormatado, fimFormatado);
    }

}
