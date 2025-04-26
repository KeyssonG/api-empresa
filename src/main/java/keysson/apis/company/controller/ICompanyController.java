package keysson.apis.company.controller;

import keysson.apis.company.dto.CompanyDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import keysson.apis.company.dto.request.RequestRegisterCompany;
import keysson.apis.company.exception.BusinessRuleException;
import org.springframework.web.bind.annotation.PostMapping;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


public interface ICompanyController {

    @PostMapping("/register")
    @Operation(
            summary = "Cadastrar uma nova empresa",
            description = "Endpoint para cadastrar uma nova empresa.",
            requestBody = @RequestBody(
                    description = "Dados da nova empresa",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RequestRegisterCompany.class)
                    )
            ),
    responses = {
            @ApiResponse(responseCode = "201", description = "Empresa cadastrada com sucesso",
                    content = @Content(schema = @Schema(implementation = CompanyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Empresa com este CNPJ já existe")
        }
    )
    void register(@RequestBody RequestRegisterCompany requestRegisterCompany) throws BusinessRuleException;

}
