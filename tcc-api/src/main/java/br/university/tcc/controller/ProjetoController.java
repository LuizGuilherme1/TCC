package br.university.tcc.controller;

import br.university.tcc.dto.ProjetoIntegradorRequest;
import br.university.tcc.dto.ProjetoResponse;
import br.university.tcc.service.ProjetoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {
    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) { this.projetoService = projetoService; }

    @PostMapping
    public ResponseEntity<ProjetoResponse> criar(@RequestBody ProjetoIntegradorRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.ok(projetoService.criar(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<ProjetoResponse>> listar(Authentication authentication) {
        boolean podeVerTodos = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("AVALIADOR") || a.getAuthority().equals("ADMINISTRADOR"));
        return ResponseEntity.ok(podeVerTodos
                ? projetoService.listarTodos()
                : projetoService.listarDoAluno(authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponse> atualizar(@PathVariable("id") Long id,
                                                      @RequestBody ProjetoIntegradorRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(projetoService.atualizar(id, request, authentication.getName()));
    }

    @PostMapping("/{id}/enviar")
    public ResponseEntity<ProjetoResponse> enviarParaAvaliacao(@PathVariable("id") Long id,
                                                                Authentication authentication) {
        return ResponseEntity.ok(projetoService.enviarParaAvaliacao(id, authentication.getName()));
    }
}