package br.university.tcc.dto;

public class RespostaAvaliacaoRequest {
    private Long formularioPerguntaId;
    private Integer pontuacao;

    public Long getFormularioPerguntaId() { return formularioPerguntaId; }
    public void setFormularioPerguntaId(Long formularioPerguntaId) { this.formularioPerguntaId = formularioPerguntaId; }
    public Integer getPontuacao() { return pontuacao; }
    public void setPontuacao(Integer pontuacao) { this.pontuacao = pontuacao; }
}
