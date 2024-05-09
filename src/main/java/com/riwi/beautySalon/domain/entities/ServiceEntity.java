package com.riwi.beautySalon.domain.entities;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity( name = "service")
@Data
@Builder //Patron de diseño para clases
/**
 *  Clase clase= new Clase
 *  clase.setName("sadsa")
 *  clase.setAge(23); 
 * 
 *  Clase.builder()
 *  .name("dsadsa")
 *  .age(12)
 *  .build();
 */
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEntity {
    @Id // -> 1
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String name;
    @Lob // -> Mapea a Text
    private String description;
    @Column(nullable = false)
    private BigDecimal price;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude //Para no crear otro espacio de memoria
    @OneToMany(
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER,
        mappedBy = "service",
        orphanRemoval = false
    )
    private List<Appointment> appointments;
}
