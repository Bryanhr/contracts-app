package pe.edu.solweb.contracts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer responseTimeHours;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyCost;

    @Column(nullable = false)
    private Integer durationMonths;
}
