package br.university.tcc.service;

import br.university.tcc.dto.AvaliacaoRequest;
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
    private final UsuarioRepository usuarioRepository;
    private final SituacaoRepository situacaoRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            FormularioRepository formularioRepository,
                            FormularioPerguntaRepository formularioPerguntaRepository,
                            RespostaAvaliacaoRepository respostaAvaliacaoRepository,
                            StatusAvaliacaoRepository statusAvaliacaoRepository,
                            ProjetoIntegradorRepository projetoRepository,
                            UsuarioRepository usuarioRepository,
                            SituacaoRepository situacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.formularioRepository = formularioRepository;
        this.formularioPerguntaRepository = formularioPerguntaRepository;
        this.respostaAvaliacaoRepository = respostaAvaliacaoRepository;
        this.statusAvaliacaoRepository = statusAvaliacaoRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
        this.situacaoRepository = situacaoRepository;
    }

        @Transactional
        public Avaliacao create(AvaliacaoRequest req, String email) {
        Usuario avaliador = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Avaliador não encontrado"));
        boolean possuiPerfil = avaliador.getUsuarioPerfis().stream()
            .anyMatch(up -> up.getPerfil() != null && ("AVALIADOR".equals(up.getPerfil().getNome()) || "ADMINISTRADOR".equals(up.getPerfil().getNome())));
        if (!possuiPerfil) throw new IllegalArgumentException("Usuário não possui perfil de avaliador");

        Formulario formulario = formularioRepository.findById(req.getFormularioId())
            .orElseThrow(() -> new IllegalArgumentException("Formulário não encontrado"));
        ProjetoIntegrador projeto = projetoRepository.findById(req.getProjetoIntegradorId())
            .orElseThrow(() -> new IllegalArgumentException("Projeto não encontrado"));
        if (projeto.getSituacao() == null || !"EM_AVALIACAO".equals(projeto.getSituacao().getNome())) {
            throw new IllegalArgumentException("O projeto não está disponível para avaliação");
        }
        if (avaliacaoRepository.findByProjetoIntegradorIdAndUsuarioAvaliadorId(projeto.getId(), avaliador.getId()).isPresent()) {
            throw new IllegalArgumentException("Você já possui uma avaliação para este projeto");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setFormulario(formulario);
        avaliacao.setProjetoIntegrador(projeto);
        avaliacao.setUsuarioAvaliador(avaliador);
        avaliacao.setStatusAvaliacao(statusAvaliacaoRepository.findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("Status PENDENTE não encontrado")));
        return avaliacaoRepository.save(avaliacao);
    }

        public List<FormularioPergunta> listarPerguntas(Long formularioId) {
        return formularioPerguntaRepository.findByFormularioIdOrderByOrdem(formularioId);
        }

        public Formulario formularioPadrao() {
            return formularioRepository.findByTitulo("Avaliação de Projeto Integrador")
                    .orElseGet(() -> formularioRepository.findFirstByAtivoTrueOrderByIdAsc()
                            .orElseThrow(() -> new IllegalArgumentException("Formulário padrão não encontrado. Execute as migrations do banco de dados.")));
        }

        public Avaliacao buscarDoProjeto(Long projetoId, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Avaliador não encontrado"));
        return avaliacaoRepository.findByProjetoIntegradorIdAndUsuarioAvaliadorId(projetoId, usuario.getId())
            .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        }

    @Transactional
    public Avaliacao iniciar(Long id, String email) {
        Avaliacao av = avaliacaoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        validarAcesso(av, email);
        if (av.getStatusAvaliacao() != null && "FINALIZADA".equals(av.getStatusAvaliacao().getNome())) {
            throw new IllegalArgumentException("Não é possível iniciar avaliação finalizada");
        }
        StatusAvaliacao emPreench = statusAvaliacaoRepository.findById(2L).orElseThrow(() -> new IllegalArgumentException("Status EM_PREENCHIMENTO não encontrado"));
        av.setDataHoraInicio(LocalDateTime.now());
        av.setStatusAvaliacao(emPreench);
        return avaliacaoRepository.save(av);
    }

    @Transactional
    public RespostaAvaliacao responder(Long avaliacaoId, RespostaAvaliacaoRequest req, String email) {
        Avaliacao av = avaliacaoRepository.findById(avaliacaoId).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        validarAcesso(av, email);
        if (av.getStatusAvaliacao() != null && "FINALIZADA".equals(av.getStatusAvaliacao().getNome())) {
            throw new IllegalArgumentException("Avaliação finalizada não pode ser alterada");
        }

        if (req.getPontuacao() == null || req.getPontuacao() < 1 || req.getPontuacao() > 5) {
            throw new IllegalArgumentException("A pontuação deve estar entre 1 e 5");
        }

        FormularioPergunta fp = formularioPerguntaRepository.findById(req.getFormularioPerguntaId()).orElseThrow(() -> new IllegalArgumentException("Pergunta do formulário não encontrada"));
        if (!fp.getFormulario().getId().equals(av.getFormulario().getId())) {
            throw new IllegalArgumentException("A pergunta não pertence ao formulário da avaliação");
        }

        RespostaAvaliacao ra = respostaAvaliacaoRepository.findByAvaliacaoIdAndFormularioPerguntaId(avaliacaoId, fp.getId())
                .orElseGet(RespostaAvaliacao::new);
        ra.setAvaliacao(av);
        ra.setFormularioPergunta(fp);
        ra.setPontuacao(req.getPontuacao());

        return respostaAvaliacaoRepository.save(ra);
    }

    @Transactional
    public Avaliacao finalizar(Long id, String observacao, String email) {
        Avaliacao av = avaliacaoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        validarAcesso(av, email);
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
    av.setObservacao(observacao);

        // update projeto situacao to AVALIADO (id 3) - simplistic rule
        ProjetoIntegrador projeto = av.getProjetoIntegrador();
        if (projeto != null) {
            projeto.setSituacao(situacaoRepository.findById(3L)
                    .orElseThrow(() -> new IllegalArgumentException("Situação AVALIADO não encontrada")));
            projetoRepository.save(projeto);
        }

        return avaliacaoRepository.save(av);
    }

    private void validarAcesso(Avaliacao avaliacao, String email) {
        if (avaliacao.getUsuarioAvaliador() == null || !email.equals(avaliacao.getUsuarioAvaliador().getEmail())) {
            throw new IllegalArgumentException("Você não tem acesso a esta avaliação");
        }
    }
}
