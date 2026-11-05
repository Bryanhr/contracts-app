package pe.edu.solweb.contracts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false, unique = true, length = 11)
    private String ruc;

    private String representative;
    private String email;
    private String phone;
    private String address;
}
