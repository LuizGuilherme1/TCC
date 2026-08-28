package br.university.tcc.controller;

import br.university.tcc.dto.AvaliacaoRequest;
import br.university.tcc.dto.RespostaAvaliacaoRequest;
import br.university.tcc.entity.Avaliacao;
import br.university.tcc.entity.RespostaAvaliacao;
import br.university.tcc.service.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {
    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) { this.avaliacaoService = avaliacaoService; }

    @PostMapping
    public ResponseEntity<Avaliacao> create(@RequestBody AvaliacaoRequest req) {
        Avaliacao a = new Avaliacao();
        // minimal create - repositories do the linking in service layer normally
        a.setFormulario(new br.university.tcc.entity.Formulario());
        a.getFormulario().setId(req.getFormularioId());
        a.setProjetoIntegrador(new br.university.tcc.entity.ProjetoIntegrador());
        a.getProjetoIntegrador().setId(req.getProjetoIntegradorId());
        a.setUsuarioAvaliador(new br.university.tcc.entity.Usuario());
        a.getUsuarioAvaliador().setId(req.getUsuarioAvaliadorId());
        a.setStatusAvaliacao(new br.university.tcc.entity.StatusAvaliacao());
        a.getStatusAvaliacao().setId(1L); // PENDENTE
        Avaliacao saved = avaliacaoService.create(a);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Avaliacao> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.iniciar(id));
    }

    @PostMapping("/{id}/respostas")
    public ResponseEntity<RespostaAvaliacao> responder(@PathVariable Long id, @RequestBody RespostaAvaliacaoRequest req) {
        RespostaAvaliacao r = avaliacaoService.responder(id, req);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Avaliacao> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.finalizar(id));
    }
}
