package br.university.tcc.controller;

import br.university.tcc.dto.AvaliacaoRequest;
import br.university.tcc.dto.RespostaAvaliacaoRequest;
import br.university.tcc.dto.FinalizarAvaliacaoRequest;
import br.university.tcc.entity.Avaliacao;
import br.university.tcc.entity.RespostaAvaliacao;
import br.university.tcc.service.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {
    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) { this.avaliacaoService = avaliacaoService; }

    @GetMapping("/formularios/{formularioId}/perguntas")
    public ResponseEntity<?> perguntas(@PathVariable("formularioId") Long formularioId) {
        return ResponseEntity.ok(avaliacaoService.listarPerguntas(formularioId));
    }

    @GetMapping("/formulario-padrao")
    public ResponseEntity<?> formularioPadrao() {
        return ResponseEntity.ok(avaliacaoService.formularioPadrao());
    }

    @GetMapping("/projetos/{projetoId}/minha")
    public ResponseEntity<Avaliacao> minha(@PathVariable("projetoId") Long projetoId, Authentication authentication) {
        return ResponseEntity.ok(avaliacaoService.buscarDoProjeto(projetoId, authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<Avaliacao> create(@RequestBody AvaliacaoRequest req, Authentication authentication) {
        return ResponseEntity.ok(avaliacaoService.create(req, authentication.getName()));
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Avaliacao> iniciar(@PathVariable("id") Long id, Authentication authentication) {
        return ResponseEntity.ok(avaliacaoService.iniciar(id, authentication.getName()));
    }

    @PostMapping("/{id}/respostas")
    public ResponseEntity<RespostaAvaliacao> responder(@PathVariable("id") Long id, @RequestBody RespostaAvaliacaoRequest req, Authentication authentication) {
        RespostaAvaliacao r = avaliacaoService.responder(id, req, authentication.getName());
        return ResponseEntity.ok(r);
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Avaliacao> finalizar(@PathVariable("id") Long id, @RequestBody(required = false) FinalizarAvaliacaoRequest req, Authentication authentication) {
        return ResponseEntity.ok(avaliacaoService.finalizar(id, req == null ? null : req.getObservacao(), authentication.getName()));
    }
}
