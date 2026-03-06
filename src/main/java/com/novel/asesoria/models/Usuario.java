package com.novel.asesoria.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombres;
    private String apellidos;
    private String universidad;
    private String carrera;
    private String username;
    private String password;
    private String rol;

    // Un usuario tiene muchos trámites
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Tramite> tramites;
}