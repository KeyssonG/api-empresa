package keysson.apis.empresa.mapper;

import keysson.apis.empresa.dto.response.EmployeeResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRowMapper implements RowMapper<EmployeeResponse> {
    @Override
    public EmployeeResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EmployeeResponse.builder()
                .id(rs.getLong("id"))
                .nome(rs.getString("nome"))
                .departamento(rs.getString("departamento"))
                .telefone(rs.getString("telefone"))
                .email(rs.getString("email"))
                .cpf(rs.getString("cpf"))
                .endereco(rs.getString("endereco"))
                .sexo(rs.getString("sexo"))
                .dataNascimento(rs.getDate("data_nascimento"))
                .dataCriacao(rs.getDate("data_criacao"))
                .companyId(rs.getLong("company_id"))
                .build();
    }
}

