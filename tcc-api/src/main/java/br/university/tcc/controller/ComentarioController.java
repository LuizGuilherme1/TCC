package br.university.tcc.controller;

import br.university.tcc.dto.ComentarioRequest;
import br.university.tcc.dto.ComentarioResponse;
import br.university.tcc.service.ComentarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos/{projetoId}/comentarios")
public class ComentarioController {
    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) { this.comentarioService = comentarioService; }

    @GetMapping
    public ResponseEntity<List<ComentarioResponse>> listar(@PathVariable("projetoId") Long projetoId,
                                                            Authentication authentication) {
        boolean podeVerTodos = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("AVALIADOR") || a.getAuthority().equals("ADMINISTRADOR"));
        return ResponseEntity.ok(comentarioService.listar(projetoId, authentication.getName(), podeVerTodos));
    }

    @PostMapping
    public ResponseEntity<ComentarioResponse> criar(@PathVariable("projetoId") Long projetoId,
                                                     @RequestBody ComentarioRequest request,
                                                     Authentication authentication) {
        boolean podeVerTodos = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("AVALIADOR") || a.getAuthority().equals("ADMINISTRADOR"));
        return ResponseEntity.ok(comentarioService.criar(projetoId, request, authentication.getName(), podeVerTodos));
    }
}