package br.university.tcc.dto;

import java.time.LocalDate;
import java.util.List;

public class ProjetoResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private Integer ano;
    private Integer semestre;
    private LocalDate dataAvaliacao;
    private String situacao;
    private List<String> alunos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
    public Integer getSemestre() { return semestre; }
    public void setSemestre(Integer semestre) { this.semestre = semestre; }
    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public List<String> getAlunos() { return alunos; }
    public void setAlunos(List<String> alunos) { this.alunos = alunos; }
}