package br.university.tcc.repository;

import br.university.tcc.entity.RespostaAvaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespostaAvaliacaoRepository extends JpaRepository<RespostaAvaliacao, Long> {
	long countByAvaliacaoId(Long avaliacaoId);
	java.util.List<RespostaAvaliacao> findByAvaliacaoId(Long avaliacaoId);
	java.util.Optional<RespostaAvaliacao> findByAvaliacaoIdAndFormularioPerguntaId(Long avaliacaoId, Long formularioPerguntaId);
}
