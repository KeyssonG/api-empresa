package keysson.apis.empresa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmpresaRegistroResultado {
    private int resultCode;
    private int idEmpresa;
}