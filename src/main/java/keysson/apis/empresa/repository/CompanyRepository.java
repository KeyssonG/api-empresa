package keysson.apis.empresa.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class CompanyRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompanyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String CHECK_EXISTS_CNPJ = """
        SELECT COUNT(*) 
        FROM companies 
        WHERE cnpj = ?
        """;

    private static final String CHECK_EXISTS_NUMERO_CONTA = """
        SELECT COUNT(*) 
        FROM companies 
        WHERE numero_conta = ?
        """;

    private static final String INSERT_COMPANY = """
        CALL proc_registrar_empresa_usuario(?, ?, ?, ?, ?, ?, ?) 
        """;

    public boolean existsByCnpj(String cnpj) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_CNPJ, Long.class, cnpj);
        return count != null && count > 0;
    }

    public boolean existsByNumeroConta(int numeroConta) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_NUMERO_CONTA, Long.class, numeroConta);
        return count != null && count > 0;
    }

    public void save(String name, String cnpj, int numeroConta, String password, String username, int status) {
        UUID consumerId = UUID.randomUUID();

        jdbcTemplate.update(INSERT_COMPANY,
                name,
                cnpj,
                numeroConta,
                status,
                consumerId,
                username,
                password
        );
    }
}
