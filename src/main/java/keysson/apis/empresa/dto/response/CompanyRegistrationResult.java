package keysson.apis.empresa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyRegistrationResult {
    private int resultCode;
    private int idEmpresa;
}