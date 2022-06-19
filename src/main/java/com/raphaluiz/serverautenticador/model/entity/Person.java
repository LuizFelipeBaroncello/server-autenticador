package com.raphaluiz.serverautenticador.model.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "PERSON")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "authToken", length = 100, nullable = false)
    private String authToken;

    @Column(name = "OTPSecret", length = 100, nullable = false)
    private String OTPSecret;
}
