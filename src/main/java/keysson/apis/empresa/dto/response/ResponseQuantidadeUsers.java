package keysson.apis.empresa.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ResponseQuantidadeUsers {

    private int quantidadeUsers;
    private Date dataCriacao;
}
