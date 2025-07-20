package keysson.apis.empresa.repository;

import keysson.apis.empresa.dto.response.EmpresaRegistroResultado;
import keysson.apis.empresa.dto.response.ResponseQuantidadeUsers;
import keysson.apis.empresa.mapper.QuantidadeUsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;


import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Repository
public class CompanyRepository {

    private final JdbcTemplate jdbcTemplate;

    public CompanyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private DataSource dataSource;

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

    private static final String SEARCH_USERS_BY_DATE = """
            SELECT data_criacao, COUNT(*) AS total_usuarios
            FROM users
            WHERE data_criacao BETWEEN ? AND ?
            GROUP BY data_criacao
            ORDER BY data_criacao;
        """;

    public boolean existsByCnpj(String cnpj) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_CNPJ, Long.class, cnpj);
        return count != null && count > 0;
    }

    public boolean existsByNumeroConta(int numeroConta) {
        Long count = jdbcTemplate.queryForObject(CHECK_EXISTS_NUMERO_CONTA, Long.class, numeroConta);
        return count != null && count > 0;
    }

    public EmpresaRegistroResultado save(String name, String email, String cnpj,
                                         int numeroConta, String password,
                                         String username, int status) {

        try {
            UUID consumerId = UUID.randomUUID();

            String sql = "CALL proc_registrar_empresa_usuario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Map<String, Object> result = jdbcTemplate.call(connection -> {
                CallableStatement cs = connection.prepareCall(sql);

                cs.setString(1, name);
                cs.setString(2, email);
                cs.setString(3, cnpj);
                cs.setInt(4, numeroConta);
                cs.setInt(5, status);
                cs.setObject(6, consumerId);
                cs.setString(7, username);
                cs.setString(8, password);

                cs.registerOutParameter(9, Types.INTEGER);
                cs.registerOutParameter(10, Types.INTEGER);

                return cs;
            }, Arrays.asList(
                    new SqlParameter("p_name", Types.VARCHAR),
                    new SqlParameter("p_email", Types.VARCHAR),
                    new SqlParameter("p_cnpj", Types.VARCHAR),
                    new SqlParameter("p_numero_conta", Types.INTEGER),
                    new SqlParameter("p_status", Types.INTEGER),
                    new SqlParameter("p_consumer_id", Types.OTHER),
                    new SqlParameter("p_username", Types.VARCHAR),
                    new SqlParameter("p_password", Types.VARCHAR),
                    new SqlOutParameter("out_result", Types.INTEGER),
                    new SqlOutParameter("out_company_id", Types.INTEGER)
            ));

            System.out.println("Map result: " + result);

            Integer resultCode = (Integer) result.get("out_result");
            Integer companyId = (Integer) result.get("out_company_id");
            return new EmpresaRegistroResultado(resultCode, companyId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar empresa: " + e.getMessage(), e);
        }
    }

    public ResponseQuantidadeUsers findUsersByDate(Date startDate, Date endDate) {
        return jdbcTemplate.query(SEARCH_USERS_BY_DATE, new Object[]{startDate, endDate}, rs -> {
            if (rs.next()) {
                return QuantidadeUsersMapper.toResponse(rs);
            }
            return null;
        });
    }
}
