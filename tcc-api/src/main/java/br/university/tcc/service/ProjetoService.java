package br.university.tcc.service;

import br.university.tcc.dto.ProjetoIntegradorRequest;
import br.university.tcc.dto.ProjetoResponse;
import br.university.tcc.entity.ProjetoAluno;
import br.university.tcc.entity.ProjetoIntegrador;
import br.university.tcc.repository.ProjetoAlunoRepository;
import br.university.tcc.repository.ProjetoIntegradorRepository;
import br.university.tcc.repository.SituacaoRepository;
import br.university.tcc.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjetoService {
    private final ProjetoIntegradorRepository projetoRepository;
    private final ProjetoAlunoRepository projetoAlunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SituacaoRepository situacaoRepository;

    public ProjetoService(ProjetoIntegradorRepository projetoRepository,
                          ProjetoAlunoRepository projetoAlunoRepository,
                          UsuarioRepository usuarioRepository,
                          SituacaoRepository situacaoRepository) {
        this.projetoRepository = projetoRepository;
        this.projetoAlunoRepository = projetoAlunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.situacaoRepository = situacaoRepository;
    }

    @Transactional
    public ProjetoResponse criar(ProjetoIntegradorRequest request, String email) {
        var aluno = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        var situacao = situacaoRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Situação CADASTRADO não encontrada"));
        ProjetoIntegrador projeto = new ProjetoIntegrador();
        projeto.setTitulo(request.getTitulo());
        projeto.setDescricao(request.getDescricao());
        projeto.setAno(request.getAno());
        projeto.setSemestre(request.getSemestre());
        projeto.setDataAvaliacao(request.getDataAvaliacao());
        projeto.setSituacao(situacao);
        ProjetoIntegrador salvo = projetoRepository.save(projeto);
        ProjetoAluno vinculo = new ProjetoAluno();
        vinculo.setProjetoIntegrador(salvo);
        vinculo.setUsuarioAluno(aluno);
        projetoAlunoRepository.save(vinculo);
        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponse> listarDoAluno(String email) {
        return projetoAlunoRepository.findByUsuarioAlunoEmail(email).stream()
                .map(ProjetoAluno::getProjetoIntegrador).map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponse> listarTodos() {
        return projetoRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ProjetoResponse atualizar(Long id, ProjetoIntegradorRequest request, String email) {
        ProjetoIntegrador projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        boolean participa = projetoAlunoRepository.findByProjetoIntegradorId(id).stream()
                .anyMatch(vinculo -> vinculo.getUsuarioAluno().getEmail().equals(email));
        if (!participa) throw new IllegalArgumentException("Você não participa deste projeto");
        if (!"CADASTRADO".equals(projeto.getSituacao().getNome())) {
            throw new IllegalArgumentException("Projeto enviado para avaliação não pode ser alterado");
        }
        projeto.setTitulo(request.getTitulo());
        projeto.setDescricao(request.getDescricao());
        projeto.setAno(request.getAno());
        projeto.setSemestre(request.getSemestre());
        projeto.setDataAvaliacao(request.getDataAvaliacao());
        return toResponse(projetoRepository.save(projeto));
    }

    @Transactional
    public ProjetoResponse enviarParaAvaliacao(Long id, String email) {
        ProjetoIntegrador projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        boolean participa = projetoAlunoRepository.findByProjetoIntegradorId(id).stream()
                .anyMatch(vinculo -> vinculo.getUsuarioAluno().getEmail().equals(email));
        if (!participa) throw new IllegalArgumentException("Você não participa deste projeto");
        if (!"CADASTRADO".equals(projeto.getSituacao().getNome())) {
            throw new IllegalArgumentException("Projeto já foi enviado para avaliação");
        }
        projeto.setSituacao(situacaoRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("Situação EM_AVALIACAO não encontrada")));
        return toResponse(projetoRepository.save(projeto));
    }

    private ProjetoResponse toResponse(ProjetoIntegrador projeto) {
        ProjetoResponse response = new ProjetoResponse();
        response.setId(projeto.getId());
        response.setTitulo(projeto.getTitulo());
        response.setDescricao(projeto.getDescricao());
        response.setAno(projeto.getAno());
        response.setSemestre(projeto.getSemestre());
        response.setDataAvaliacao(projeto.getDataAvaliacao());
        response.setSituacao(projeto.getSituacao().getNome());
        response.setAlunos(projetoAlunoRepository.findByProjetoIntegradorId(projeto.getId()).stream()
                .map(vinculo -> vinculo.getUsuarioAluno().getNome()).collect(Collectors.toList()));
        return response;
    }
}