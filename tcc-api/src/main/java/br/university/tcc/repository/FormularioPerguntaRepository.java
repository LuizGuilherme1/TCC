package br.university.tcc.repository;

import java.util.List;

import br.university.tcc.entity.FormularioPergunta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormularioPerguntaRepository extends JpaRepository<FormularioPergunta, Long> {
	List<FormularioPergunta> findByFormularioIdOrderByOrdem(Long formularioId);
	long countByFormularioId(Long formularioId);
}
