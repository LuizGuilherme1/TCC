package br.university.tcc.dto;

public class AvaliacaoRequest {
    private Long formularioId;
    private Long projetoIntegradorId;
    private Long usuarioAvaliadorId;

    public Long getFormularioId() { return formularioId; }
    public void setFormularioId(Long formularioId) { this.formularioId = formularioId; }
    public Long getProjetoIntegradorId() { return projetoIntegradorId; }
    public void setProjetoIntegradorId(Long projetoIntegradorId) { this.projetoIntegradorId = projetoIntegradorId; }
    public Long getUsuarioAvaliadorId() { return usuarioAvaliadorId; }
    public void setUsuarioAvaliadorId(Long usuarioAvaliadorId) { this.usuarioAvaliadorId = usuarioAvaliadorId; }
}
