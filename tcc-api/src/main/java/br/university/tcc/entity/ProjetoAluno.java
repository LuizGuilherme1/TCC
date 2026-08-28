package br.university.tcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projeto_aluno", uniqueConstraints = {@UniqueConstraint(columnNames = {"projeto_integrador_id", "usuario_aluno_id"})})
public class ProjetoAluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projeto_integrador_id", nullable = false)
    private ProjetoIntegrador projetoIntegrador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_aluno_id", nullable = false)
    private Usuario usuarioAluno;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public ProjetoAluno() {}

    @PrePersist
    public void prePersist() { criadoEm = atualizadoEm = LocalDateTime.now(); }

    @PreUpdate
    public void preUpdate() { atualizadoEm = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProjetoIntegrador getProjetoIntegrador() { return projetoIntegrador; }
    public void setProjetoIntegrador(ProjetoIntegrador projetoIntegrador) { this.projetoIntegrador = projetoIntegrador; }
    public Usuario getUsuarioAluno() { return usuarioAluno; }
    public void setUsuarioAluno(Usuario usuarioAluno) { this.usuarioAluno = usuarioAluno; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
