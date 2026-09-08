package br.university.tcc.controller;

import br.university.tcc.dto.UsuarioRequest;
import br.university.tcc.dto.UsuarioResponse;
import br.university.tcc.entity.Usuario;
import br.university.tcc.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) { this.usuarioService = usuarioService; }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> list() {
        List<UsuarioResponse> resp = usuarioService.listAll().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> get(@PathVariable("id") Long id) {
        Usuario u = usuarioService.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return ResponseEntity.ok(toResponse(u));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@RequestBody UsuarioRequest req) {
        Usuario u = usuarioService.createUsuario(req, List.of("ALUNO"));
        return ResponseEntity.ok(toResponse(u));
    }

    @PostMapping("/{id}/promover-professor")
    public ResponseEntity<UsuarioResponse> promoverParaProfessor(@PathVariable("id") Long id) {
        return ResponseEntity.ok(toResponse(usuarioService.promoverParaProfessor(id)));
    }

    private UsuarioResponse toResponse(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setNome(u.getNome());
        r.setEmail(u.getEmail());
        r.setPerfis(u.getUsuarioPerfis().stream().map(up -> up.getPerfil().getNome()).collect(Collectors.toList()));
        return r;
    }
}
