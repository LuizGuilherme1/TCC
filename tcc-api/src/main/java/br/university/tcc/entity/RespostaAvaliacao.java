package br.university.tcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resposta_avaliacao", uniqueConstraints = {@UniqueConstraint(columnNames = {"avaliacao_id", "formulario_pergunta_id"})})
public class RespostaAvaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "avaliacao_id", nullable = false)
    private Avaliacao avaliacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formulario_pergunta_id", nullable = false)
    private FormularioPergunta formularioPergunta;

    @Column(nullable = false)
    private Integer pontuacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public RespostaAvaliacao() {}

    @PrePersist
    public void prePersist() { criadoEm = atualizadoEm = LocalDateTime.now(); }

    @PreUpdate
    public void preUpdate() { atualizadoEm = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Avaliacao getAvaliacao() { return avaliacao; }
    public void setAvaliacao(Avaliacao avaliacao) { this.avaliacao = avaliacao; }
    public FormularioPergunta getFormularioPergunta() { return formularioPergunta; }
    public void setFormularioPergunta(FormularioPergunta formularioPergunta) { this.formularioPergunta = formularioPergunta; }
    public Integer getPontuacao() { return pontuacao; }
    public void setPontuacao(Integer pontuacao) { this.pontuacao = pontuacao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
