package br.university.tcc.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UsuarioPerfilId implements Serializable {
    private Long usuarioId;
    private Long perfilId;

    public UsuarioPerfilId() {}

    public UsuarioPerfilId(Long usuarioId, Long perfilId) {
        this.usuarioId = usuarioId;
        this.perfilId = perfilId;
    }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getPerfilId() { return perfilId; }
    public void setPerfilId(Long perfilId) { this.perfilId = perfilId; }
}
