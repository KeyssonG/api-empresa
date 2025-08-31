package keysson.apis.empresa.mapper;

import keysson.apis.empresa.dto.response.UserCountResponse;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCountMapper {
    public static UserCountResponse toResponse(ResultSet rs) throws SQLException {
        return UserCountResponse.builder()
                .quantidadeUsuarios(rs.getInt("total_usuarios"))
                .dataCriacao(rs.getDate("data_criacao"))
                .build();
    }
}
