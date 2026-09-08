package br.university.tcc.repository;

import br.university.tcc.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByProjetoIdOrderByCriadoEmAsc(Long projetoId);
}