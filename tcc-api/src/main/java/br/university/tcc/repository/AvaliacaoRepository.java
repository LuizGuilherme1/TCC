package br.university.tcc.repository;

import br.university.tcc.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
	java.util.Optional<Avaliacao> findByProjetoIntegradorIdAndUsuarioAvaliadorId(Long projetoId, Long avaliadorId);
	java.util.List<Avaliacao> findByProjetoIntegradorIdAndStatusAvaliacaoNomeOrderByIdAsc(Long projetoId, String status);
}
