package com.salon.manager.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false)
    private Integer duracionMin;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Boolean activo = true;
}
