package br.university.tcc.dto;

import java.math.BigDecimal;
import java.util.List;

public class AvaliacaoProjetoResponse {
    private Long id;
    private String avaliador;
    private Integer pontuacaoTotal;
    private BigDecimal media;
    private String observacao;
    private List<RespostaAvaliacaoResponse> respostas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAvaliador() { return avaliador; }
    public void setAvaliador(String avaliador) { this.avaliador = avaliador; }
    public Integer getPontuacaoTotal() { return pontuacaoTotal; }
    public void setPontuacaoTotal(Integer pontuacaoTotal) { this.pontuacaoTotal = pontuacaoTotal; }
    public BigDecimal getMedia() { return media; }
    public void setMedia(BigDecimal media) { this.media = media; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public List<RespostaAvaliacaoResponse> getRespostas() { return respostas; }
    public void setRespostas(List<RespostaAvaliacaoResponse> respostas) { this.respostas = respostas; }
}
