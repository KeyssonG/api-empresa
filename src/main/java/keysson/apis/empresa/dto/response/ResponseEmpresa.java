package keysson.apis.empresa.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseEmpresa {
    private String nome;
    private int conta;
}
