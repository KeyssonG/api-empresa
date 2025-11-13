package keysson.apis.empresa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import keysson.apis.empresa.dto.request.RequestRegisterCompany;
import keysson.apis.empresa.dto.response.CompanyResponse;
import keysson.apis.empresa.dto.response.EmployeeResponse;
import keysson.apis.empresa.dto.response.UserCountResponse;
import keysson.apis.empresa.exception.BusinessRuleException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;


public interface CompanyController {

    @PostMapping("/register")
    @Operation(
            summary = "Cadastrar uma nova empresa",
            description = "Endpoint para cadastrar uma nova empresa, cria usuário administrativo.",
            requestBody = @RequestBody(
                    description = "Dados da nova empresa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RequestRegisterCompany.class)
                    )
            )
    )
   public CompanyResponse register(@RequestBody RequestRegisterCompany requestRegisterCompany)
            throws BusinessRuleException, SQLException;

    @GetMapping("/users")
    @Operation(
            summary = "Obtém a quantidade de usuários cadastrados",
            description = "Endpoint para obter a quantidade de usuários cadastrados por data."
    )
    UserCountResponse searchUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    ) throws BusinessRuleException, SQLException;

    @GetMapping("/employees/date")
    @Operation(
        summary = "Busca funcionários por data",
        description = "Busca funcionários por data de início e fim. Se não informado, assume a data atual."
    )
    List<EmployeeResponse> searchEmployeesByDate(
        @RequestHeader("Authorization") String token,
        @RequestParam(required = false) String dataInicio,
        @RequestParam(required = false) String dataFim
    ) throws BusinessRuleException, SQLException;

    @GetMapping("/employees/{departamento}/date")
    @Operation(
        summary = "Busca funcionários por departamento e data",
        description = "Busca funcionários por departamento e data de início e fim. Se não informado, assume a data atual."
    )
    List<EmployeeResponse> searchEmployeesByDepartmentAndDate(
        @RequestHeader("Authorization") String token,
        @PathVariable String departamento,
        @RequestParam(required = false) String dataInicio,
        @RequestParam(required = false) String dataFim
    ) throws BusinessRuleException, SQLException;

}
