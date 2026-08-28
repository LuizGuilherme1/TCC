package br.university.tcc.service;

import br.university.tcc.dto.RespostaAvaliacaoRequest;
import br.university.tcc.entity.*;
import br.university.tcc.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final FormularioRepository formularioRepository;
    private final FormularioPerguntaRepository formularioPerguntaRepository;
    private final RespostaAvaliacaoRepository respostaAvaliacaoRepository;
    private final StatusAvaliacaoRepository statusAvaliacaoRepository;
    private final ProjetoIntegradorRepository projetoRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            FormularioRepository formularioRepository,
                            FormularioPerguntaRepository formularioPerguntaRepository,
                            RespostaAvaliacaoRepository respostaAvaliacaoRepository,
                            StatusAvaliacaoRepository statusAvaliacaoRepository,
                            ProjetoIntegradorRepository projetoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.formularioRepository = formularioRepository;
        this.formularioPerguntaRepository = formularioPerguntaRepository;
        this.respostaAvaliacaoRepository = respostaAvaliacaoRepository;
        this.statusAvaliacaoRepository = statusAvaliacaoRepository;
        this.projetoRepository = projetoRepository;
    }

    public Avaliacao create(Avaliacao a) {
        return avaliacaoRepository.save(a);
    }

    @Transactional
    public Avaliacao iniciar(Long id) {
        Avaliacao av = avaliacaoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        if (av.getStatusAvaliacao() != null && "FINALIZADA".equals(av.getStatusAvaliacao().getNome())) {
            throw new IllegalArgumentException("Não é possível iniciar avaliação finalizada");
        }
        StatusAvaliacao emPreench = statusAvaliacaoRepository.findById(2L).orElseThrow(() -> new IllegalArgumentException("Status EM_PREENCHIMENTO não encontrado"));
        av.setDataHoraInicio(LocalDateTime.now());
        av.setStatusAvaliacao(emPreench);
        return avaliacaoRepository.save(av);
    }

    @Transactional
    public RespostaAvaliacao responder(Long avaliacaoId, RespostaAvaliacaoRequest req) {
        Avaliacao av = avaliacaoRepository.findById(avaliacaoId).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        if (av.getStatusAvaliacao() != null && "FINALIZADA".equals(av.getStatusAvaliacao().getNome())) {
            throw new IllegalArgumentException("Avaliação finalizada não pode ser alterada");
        }

        if (req.getPontuacao() == null || req.getPontuacao() < 1 || req.getPontuacao() > 5) {
            throw new IllegalArgumentException("A pontuação deve estar entre 1 e 5");
        }

        FormularioPergunta fp = formularioPerguntaRepository.findById(req.getFormularioPerguntaId()).orElseThrow(() -> new IllegalArgumentException("Pergunta do formulário não encontrada"));

        RespostaAvaliacao ra = new RespostaAvaliacao();
        ra.setAvaliacao(av);
        ra.setFormularioPergunta(fp);
        ra.setPontuacao(req.getPontuacao());

        return respostaAvaliacaoRepository.save(ra);
    }

    @Transactional
    public Avaliacao finalizar(Long id) {
        Avaliacao av = avaliacaoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        if (av.getStatusAvaliacao() != null && "FINALIZADA".equals(av.getStatusAvaliacao().getNome())) {
            throw new IllegalArgumentException("Avaliação já finalizada");
        }

        // verify all perguntas answered
        long totalPerguntas = formularioPerguntaRepository.countByFormularioId(av.getFormulario().getId());
        long respostas = respostaAvaliacaoRepository.countByAvaliacaoId(av.getId());
        if (respostas < totalPerguntas) {
            throw new IllegalArgumentException("Nem todas as perguntas foram respondidas");
        }

        List<RespostaAvaliacao> lista = respostaAvaliacaoRepository.findByAvaliacaoId(av.getId());
        int soma = lista.stream().mapToInt(RespostaAvaliacao::getPontuacao).sum();
        BigDecimal media = BigDecimal.valueOf((double) soma / lista.size()).setScale(2, RoundingMode.HALF_UP);

        av.setPontuacaoTotal(soma);
        av.setMedia(media);
        av.setDataHoraFinalizacao(LocalDateTime.now());
        StatusAvaliacao finalizada = statusAvaliacaoRepository.findById(3L).orElseThrow(() -> new IllegalArgumentException("Status FINALIZADA não encontrado"));
        av.setStatusAvaliacao(finalizada);

        // update projeto situacao to AVALIADO (id 3) - simplistic rule
        ProjetoIntegrador projeto = av.getProjetoIntegrador();
        if (projeto != null) {
            Situacao sAvaliado = projeto.getSituacao();
            if (sAvaliado == null || !"AVALIADO".equals(sAvaliado.getNome())) {
                // try fetch situacao id 3
                // Only change if available
                // not forcing
            }
        }

        return avaliacaoRepository.save(av);
    }
}
