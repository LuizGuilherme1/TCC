package br.university.tcc.repository;

import br.university.tcc.entity.ProjetoAluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjetoAlunoRepository extends JpaRepository<ProjetoAluno, Long> {
}
