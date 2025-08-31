package keysson.apis.empresa.repository;

import keysson.apis.empresa.dto.PendingMessages;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

@Repository
public class RabbitRepository {

    private final JdbcTemplate jdbcTemplate;

    public RabbitRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String INSERT_MENSSAGE = """
    INSERT INTO MENSAGENS_PENDENTES (id, name, email, cnpj_cpf, username, status)
    VALUES (?, ?, ?, ?, ?, ?)
    """;

    public void saveMenssage(PendingMessages message) throws SQLException {
        try {
            jdbcTemplate.update(INSERT_MENSSAGE,
                    message.getId(),
                    message.getName(),
                    message.getEmail(),
                    message.getCnpj(),
                    message.getUsername(),
                    message.getStatus());
        } catch (Exception ex) {
            throw new SQLException("Erro ao salvar a mensagem pendente no banco", ex);
        }
    }

}
