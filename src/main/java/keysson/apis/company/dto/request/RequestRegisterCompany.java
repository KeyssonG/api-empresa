package keysson.apis.company.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestRegisterCompany {
    @NotBlank(message = "O nome da empresa é obrigatório")
    private String name;

    @NotBlank(message = "O CNPJ é obrigatório, somente números")
    @Size(min = 14, max = 18, message = "O CNPJ deve ter entre 14 e 18 caracteres")
    private String cnpj;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String password;
}
