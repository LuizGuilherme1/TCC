package br.university.tcc.repository;

import br.university.tcc.entity.Formulario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormularioRepository extends JpaRepository<Formulario, Long> {
	java.util.Optional<Formulario> findByTitulo(String titulo);
	java.util.Optional<Formulario> findFirstByAtivoTrueOrderByIdAsc();
}
