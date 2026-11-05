package pe.edu.solweb.contracts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"plan_id", "support_service_id"}))
@Getter
@Setter
public class PlanSupportService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "support_service_id")
    private SupportService supportService;

    private Integer monthlyLimit;

    @Column(precision = 10, scale = 2)
    private BigDecimal extraCost;
}
