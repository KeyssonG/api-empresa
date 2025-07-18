package keysson.apis.empresa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import keysson.apis.empresa.dto.request.RequestRegisterCompany;
import keysson.apis.empresa.dto.response.ResponseEmpresa;
import keysson.apis.empresa.dto.response.ResponseQuantidadeUsers;
import keysson.apis.empresa.exception.BusinessRuleException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;


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
   public ResponseEmpresa register(@RequestBody RequestRegisterCompany requestRegisterCompany)
            throws BusinessRuleException, SQLException;

    @GetMapping("/users")
    @Operation(
            summary = "Obtém a quantidade de usuários cadastrados",
            description = "Endpoint para obter a quantidade de usuários cadastrados por data."
    )
    ResponseQuantidadeUsers searchUsers(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim
    ) throws BusinessRuleException, SQLException;
}
