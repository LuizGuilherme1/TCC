package br.university.tcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "formulario_pergunta",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"formulario_id", "pergunta_id"}),
           @UniqueConstraint(columnNames = {"formulario_id", "ordem"})
       })
public class FormularioPergunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formulario_id", nullable = false)
    private Formulario formulario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pergunta_id", nullable = false)
    private Pergunta pergunta;

    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public FormularioPergunta() {}

    @PrePersist
    public void prePersist() { criadoEm = atualizadoEm = LocalDateTime.now(); }

    @PreUpdate
    public void preUpdate() { atualizadoEm = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Formulario getFormulario() { return formulario; }
    public void setFormulario(Formulario formulario) { this.formulario = formulario; }
    public Pergunta getPergunta() { return pergunta; }
    public void setPergunta(Pergunta pergunta) { this.pergunta = pergunta; }
    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
