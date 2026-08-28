package br.university.tcc.dto;

import java.util.List;

public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private List<String> perfis;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getPerfis() { return perfis; }
    public void setPerfis(List<String> perfis) { this.perfis = perfis; }
}
