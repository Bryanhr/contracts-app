package pe.edu.solweb.contracts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Contract {

    static final int EXPIRING_DAYS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status;

    private LocalDate signedDate;

    public Validity validityOn(LocalDate today) {
        if (status != ContractStatus.SIGNED) return null;
        if (today.isBefore(startDate)) return Validity.PENDING;
        if (today.isAfter(endDate)) return Validity.EXPIRED;
        if (!endDate.isAfter(today.plusDays(EXPIRING_DAYS))) return Validity.EXPIRING;
        return Validity.ACTIVE;
    }
}
