package keysson.apis.company.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "companies", uniqueConstraints = {
            @UniqueConstraint(columnNames = "cnpj")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @SequenceGenerator(name = "company_seq", sequenceName = "company_sequence", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(nullable = false)
    private String password;

    private int status;

    @Column(name = "consumer_id", nullable = false, unique = true)
    @Builder.Default
    private UUID consumerId = UUID.randomUUID();

}
