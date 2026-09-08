package br.university.tcc.repository;

import br.university.tcc.entity.ProjetoAluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjetoAlunoRepository extends JpaRepository<ProjetoAluno, Long> {
	List<ProjetoAluno> findByUsuarioAlunoEmail(String email);
	List<ProjetoAluno> findByProjetoIntegradorId(Long projetoId);
}
