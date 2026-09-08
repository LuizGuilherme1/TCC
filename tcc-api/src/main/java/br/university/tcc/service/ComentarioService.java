package br.university.tcc.service;

import br.university.tcc.dto.ComentarioRequest;
import br.university.tcc.dto.ComentarioResponse;
import br.university.tcc.entity.Comentario;
import br.university.tcc.repository.ComentarioRepository;
import br.university.tcc.repository.ProjetoAlunoRepository;
import br.university.tcc.repository.ProjetoIntegradorRepository;
import br.university.tcc.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final ProjetoIntegradorRepository projetoRepository;
    private final ProjetoAlunoRepository projetoAlunoRepository;
    private final UsuarioRepository usuarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository,
                             ProjetoIntegradorRepository projetoRepository,
                             ProjetoAlunoRepository projetoAlunoRepository,
                             UsuarioRepository usuarioRepository) {
        this.comentarioRepository = comentarioRepository;
        this.projetoRepository = projetoRepository;
        this.projetoAlunoRepository = projetoAlunoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<ComentarioResponse> listar(Long projetoId, String email, boolean podeVerTodos) {
        if (!podeVerTodos && projetoAlunoRepository.findByProjetoIntegradorId(projetoId).stream()
                .noneMatch(vinculo -> vinculo.getUsuarioAluno().getEmail().equals(email))) {
            throw new IllegalArgumentException("Você não participa deste projeto");
        }
        return comentarioRepository.findByProjetoIdOrderByCriadoEmAsc(projetoId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ComentarioResponse criar(Long projetoId, ComentarioRequest request, String email, boolean podeVerTodos) {
        var projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        var autor = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        if (!podeVerTodos && projetoAlunoRepository.findByProjetoIntegradorId(projetoId).stream()
                .noneMatch(vinculo -> vinculo.getUsuarioAluno().getEmail().equals(email))) {
            throw new IllegalArgumentException("Você não participa deste projeto");
        }
        if (!"CADASTRADO".equals(projeto.getSituacao().getNome())) {
            throw new IllegalArgumentException("Não é permitido comentar após o envio para avaliação");
        }
        if (request.getTexto() == null || request.getTexto().isBlank()) {
            throw new IllegalArgumentException("O comentário não pode ficar vazio");
        }
        Comentario comentario = new Comentario();
        comentario.setProjeto(projeto);
        comentario.setAutor(autor);
        comentario.setTexto(request.getTexto().trim());
        return toResponse(comentarioRepository.save(comentario));
    }

    private ComentarioResponse toResponse(Comentario comentario) {
        ComentarioResponse response = new ComentarioResponse();
        response.setId(comentario.getId());
        response.setTexto(comentario.getTexto());
        response.setAutor(comentario.getAutor().getNome());
        response.setCriadoEm(comentario.getCriadoEm());
        return response;
    }
}