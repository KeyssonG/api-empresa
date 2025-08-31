package keysson.apis.empresa.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class UserCountResponse {

    private int quantidadeUsuarios;
    private Date dataCriacao;
}
