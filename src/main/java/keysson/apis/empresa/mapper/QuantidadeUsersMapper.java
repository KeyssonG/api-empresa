package keysson.apis.empresa.mapper;

import keysson.apis.empresa.dto.response.ResponseQuantidadeUsers;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuantidadeUsersMapper {
    public static ResponseQuantidadeUsers toResponse(ResultSet rs) throws SQLException {
        return ResponseQuantidadeUsers.builder()
                .quantidadeUsers(rs.getInt("total_usuarios"))
                .dataCriacao(rs.getDate("data_criacao"))
                .build();
    }
}
