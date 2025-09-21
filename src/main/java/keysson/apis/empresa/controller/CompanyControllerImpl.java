package keysson.apis.empresa.controller;

import keysson.apis.empresa.config.FormatDate;
import keysson.apis.empresa.dto.request.RequestRegisterCompany;
import keysson.apis.empresa.dto.response.CompanyResponse;
import keysson.apis.empresa.dto.response.EmployeeResponse;
import keysson.apis.empresa.dto.response.UserCountResponse;
import keysson.apis.empresa.exception.BusinessRuleException;
import keysson.apis.empresa.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
public class CompanyControllerImpl implements CompanyController{

    private static final Logger logger = LoggerFactory.getLogger(CompanyControllerImpl.class);

    private final CompanyService companyService;

    @Autowired
    public CompanyControllerImpl(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public CompanyResponse register(@RequestBody RequestRegisterCompany requestRegisterCompany) throws SQLException {
        logger.info("Registrando empresa com os dados: {}", requestRegisterCompany);
        CompanyResponse response = companyService.registerCompany(requestRegisterCompany);
        logger.info("Empresa registrada com sucesso: {}", response);
        return response;
    }

    @Override
    public UserCountResponse searchUsers(String token,
                                         @RequestParam(required = false) String dataInicio,
                                         @RequestParam(required = false) String dataFim
    ) throws BusinessRuleException, SQLException {
        logger.info("Buscando usuários com token: {}, dataInicio: {}, dataFim: {}", token, dataInicio, dataFim);
        if (dataInicio == null || dataInicio.isEmpty()) {
            dataInicio = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        if (dataFim == null || dataFim.isEmpty()) {
            dataFim = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        java.util.Date inicioFormatado = FormatDate.formatDate(dataInicio);
        java.util.Date fimFormatado = FormatDate.formatDate(dataFim);
        UserCountResponse response = companyService.searchUsersByDate(inicioFormatado, fimFormatado);
        logger.info("Busca de usuários concluída com sucesso: {}", response);
        return response;
    }

    @Override
    public List<EmployeeResponse> searchEmployeesByDate(String token, @RequestParam(required = false) String dataInicio, @RequestParam(required = false) String dataFim) throws BusinessRuleException, SQLException {
        logger.info("Buscando funcionários por data: dataInicio={}, dataFim={}", dataInicio, dataFim);
        if (dataInicio == null || dataInicio.isEmpty()) {
            dataInicio = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        if (dataFim == null || dataFim.isEmpty()) {
            dataFim = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        java.util.Date inicioFormatado = FormatDate.formatDate(dataInicio);
        java.util.Date fimFormatado = FormatDate.formatDate(dataFim);
        return companyService.searchEmployeesByDepartmentAndDate(null, inicioFormatado, fimFormatado);
    }

    @Override
    public List<EmployeeResponse> searchEmployeesByDepartmentAndDate(String token, String departamento, @RequestParam(required = false) String dataInicio, @RequestParam(required = false) String dataFim) throws BusinessRuleException, SQLException {
        logger.info("Buscando funcionários por departamento: {} e data: dataInicio={}, dataFim={}", departamento, dataInicio, dataFim);
        if (dataInicio == null || dataInicio.isEmpty()) {
            dataInicio = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        if (dataFim == null || dataFim.isEmpty()) {
            dataFim = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        }
        java.util.Date inicioFormatado = FormatDate.formatDate(dataInicio);
        java.util.Date fimFormatado = FormatDate.formatDate(dataFim);
        return companyService.searchEmployeesByDepartmentAndDate(departamento, inicioFormatado, fimFormatado);
    }

}
