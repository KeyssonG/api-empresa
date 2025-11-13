package keysson.apis.empresa.repository;

import keysson.apis.empresa.dto.response.CompanyRegistrationResult;
import keysson.apis.empresa.dto.response.UserCountResponse;
import keysson.apis.empresa.dto.response.EmployeeResponse;
import keysson.apis.empresa.mapper.UserCountMapper;
import keysson.apis.empresa.mapper.EmployeeRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class CompanyRepository {

    private static final Logger logger = LoggerFactory.getLogger(CompanyRepository.class);

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

    private static final String SEARCH_USERS_BY_DATE = """
                SELECT data_criacao, COUNT(*) AS total_usuarios
                FROM users
                WHERE data_criacao BETWEEN ? AND ?
                ORDER BY data_criacao;
            """;

    private static final String SEARCH_EMPLOYEES_BY_DEPARTMENT_AND_DATE_BASE = """
                    SELECT
                f.id,
                f.nome,
                f.departamento,
                c.telefone,
                c.email,
                f.cpf,
                e.endereco,
                f.sexo,
                f.data_nascimento,
                f.data_criacao,
                f.company_id
            FROM funcionarios f
            JOIN contatos c ON c.user_id  = f.id
            JOIN enderecamento e ON e.id  = f.id
                    WHERE f.data_criacao BETWEEN ? AND ? AND f.company_id = ?
                    """;

    public boolean existsByCnpj(String cnpj) {
        logger.info("Verificando existência de empresa com CNPJ: {}", cnpj);
        Integer count = jdbcTemplate.queryForObject(CHECK_EXISTS_CNPJ, Integer.class, cnpj);
        boolean exists = count != null && count > 0;
        logger.debug("Resultado da verificação de CNPJ: {}", exists);
        return exists;
    }

    public boolean existsByNumeroConta(int numeroConta) {
        logger.info("Verificando existência de número de conta: {}", numeroConta);
        Integer count = jdbcTemplate.queryForObject(CHECK_EXISTS_NUMERO_CONTA, Integer.class, numeroConta);
        boolean exists = count != null && count > 0;
        logger.debug("Resultado da verificação de número de conta: {}", exists);
        return exists;
    }

    public CompanyRegistrationResult save(String name, String email, String cnpj,
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
                    new SqlOutParameter("out_company_id", Types.INTEGER)));

            logger.debug("Map result: {}", result);

            Integer resultCode = (Integer) result.get("out_result");
            Integer companyId = (Integer) result.get("out_company_id");
            return new CompanyRegistrationResult(resultCode, companyId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar empresa: " + e.getMessage(), e);
        }
    }

    public UserCountResponse findUsersByDate(Date startDate, Date endDate) {
        logger.info("Buscando usuários entre as datas: {} e {}", startDate, endDate);
        try {
            return jdbcTemplate.query(SEARCH_USERS_BY_DATE, ps -> {
                ps.setDate(1, new java.sql.Date(startDate.getTime()));
                ps.setDate(2, new java.sql.Date(endDate.getTime()));
            }, rs -> {
                if (rs.next()) {
                    return UserCountMapper.toResponse(rs);
                }
                return null;
            });
        } catch (Exception e) {
            logger.error("Erro ao buscar usuários entre as datas: {} e {}. Detalhes: {}", startDate, endDate,
                    e.getMessage());
            throw new RuntimeException("Erro ao buscar usuários por data", e);
        }
    }

    public List<EmployeeResponse> findEmployeesByDepartmentAndDate(String departamento, Date startDate, Date endDate,
            Integer idEmpresa) {
        logger.info("Buscando funcionários entre as datas: {} e {}, departamento: {} e company_id: {}", startDate,
                endDate, departamento, idEmpresa);

        StringBuilder sql = new StringBuilder(SEARCH_EMPLOYEES_BY_DEPARTMENT_AND_DATE_BASE.trim());
        List<Object> params = new ArrayList<>();
        List<Integer> types = new ArrayList<>();

        // Parâmetros base: startDate, endDate, idEmpresa
        params.add(new java.sql.Date(startDate.getTime()));
        params.add(new java.sql.Date(endDate.getTime()));
        params.add(idEmpresa);
        types.add(Types.DATE);
        types.add(Types.DATE);
        types.add(Types.INTEGER);

        // Adiciona filtro de departamento se fornecido
        if (departamento != null && !departamento.isEmpty()) {
            sql.append(" AND departamento = ?");
            params.add(departamento);
            types.add(Types.VARCHAR);
        }

        sql.append(" ORDER BY data_criacao");

        try {
            return jdbcTemplate.query(
                    sql.toString(),
                    params.toArray(),
                    types.stream().mapToInt(i -> i).toArray(),
                    new EmployeeRowMapper());
        } catch (Exception e) {
            logger.error("Erro ao buscar funcionários: {}", e.getMessage());
            throw new RuntimeException("Erro ao buscar funcionários por departamento, data e empresa", e);
        }
    }
}
