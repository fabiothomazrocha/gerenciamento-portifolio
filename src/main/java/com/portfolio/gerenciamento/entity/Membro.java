package com.portfolio.gerenciamento.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "membros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Membro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "id_externo", nullable = false, unique = true)
    private String idExterno;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String atribuicao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @ManyToMany(mappedBy = "membros", fetch = FetchType.LAZY)
    private Set<Projeto> projetos = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }

    public boolean isFuncionario() {
        return "funcionário".equalsIgnoreCase(this.atribuicao)
                || "funcionario".equalsIgnoreCase(this.atribuicao);
    }
}
