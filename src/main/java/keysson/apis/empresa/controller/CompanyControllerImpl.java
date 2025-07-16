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
    public ResponseEmpresa register(@RequestBody RequestRegisterCompany requestRegisterCompany) throws SQLException {
           return companyService.registerCompany(requestRegisterCompany);
    }

    @Override
    public ResponseQuantidadeUsers searchUsers(String token,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    ) throws BusinessRuleException, SQLException {
        if (dataInicio == null || dataInicio.isEmpty()) {
            dataInicio = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        if (dataFim == null || dataFim.isEmpty()) {
            dataFim = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        java.util.Date inicioFormatado = FormatDate.formatDate(dataInicio);
        java.util.Date fimFormatado = FormatDate.formatDate(dataFim);
        return companyService.searchUsersByDate(inicioFormatado, fimFormatado);
    }

}
