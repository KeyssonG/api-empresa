package keysson.apis.empresa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredCompanyEvent {
    private int idEmpresa;
    private String name;
    private String email;
    private String cnpj;
    private String username;
}