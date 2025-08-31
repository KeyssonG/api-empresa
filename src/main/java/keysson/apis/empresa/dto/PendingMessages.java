package keysson.apis.empresa.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingMessages {
    private int id;
    private String name;
    private String email;
    private String cnpj;
    private String username;
    private int status;
}
