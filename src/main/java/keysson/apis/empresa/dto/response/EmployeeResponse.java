package keysson.apis.empresa.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class EmployeeResponse {
    private Long id;
    private String nome;
    private String departamento;
    private Date dataCriacao;
    private Long companyId;
}

