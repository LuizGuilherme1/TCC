package br.university.tcc.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacao", uniqueConstraints = {@UniqueConstraint(columnNames = {"projeto_integrador_id", "usuario_avaliador_id"})})
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formulario_id", nullable = false)
    private Formulario formulario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projeto_integrador_id", nullable = false)
    private ProjetoIntegrador projetoIntegrador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_avaliador_id", nullable = false)
    private Usuario usuarioAvaliador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_avaliacao_id", nullable = false)
    private StatusAvaliacao statusAvaliacao;

    @Column(name = "data_hora_inicio")
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_finalizacao")
    private LocalDateTime dataHoraFinalizacao;

    @Column(name = "pontuacao_total")
    private Integer pontuacaoTotal;

    @Column(precision = 3, scale = 2)
    private BigDecimal media;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    public Avaliacao() {}

    @PrePersist
    public void prePersist() { criadoEm = atualizadoEm = LocalDateTime.now(); }

    @PreUpdate
    public void preUpdate() { atualizadoEm = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Formulario getFormulario() { return formulario; }
    public void setFormulario(Formulario formulario) { this.formulario = formulario; }
    public ProjetoIntegrador getProjetoIntegrador() { return projetoIntegrador; }
    public void setProjetoIntegrador(ProjetoIntegrador projetoIntegrador) { this.projetoIntegrador = projetoIntegrador; }
    public Usuario getUsuarioAvaliador() { return usuarioAvaliador; }
    public void setUsuarioAvaliador(Usuario usuarioAvaliador) { this.usuarioAvaliador = usuarioAvaliador; }
    public StatusAvaliacao getStatusAvaliacao() { return statusAvaliacao; }
    public void setStatusAvaliacao(StatusAvaliacao statusAvaliacao) { this.statusAvaliacao = statusAvaliacao; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public LocalDateTime getDataHoraFinalizacao() { return dataHoraFinalizacao; }
    public void setDataHoraFinalizacao(LocalDateTime dataHoraFinalizacao) { this.dataHoraFinalizacao = dataHoraFinalizacao; }
    public Integer getPontuacaoTotal() { return pontuacaoTotal; }
    public void setPontuacaoTotal(Integer pontuacaoTotal) { this.pontuacaoTotal = pontuacaoTotal; }
    public BigDecimal getMedia() { return media; }
    public void setMedia(BigDecimal media) { this.media = media; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
