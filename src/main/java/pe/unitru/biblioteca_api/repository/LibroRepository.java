package pe.unitru.biblioteca_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.unitru.biblioteca_api.model.Libro;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

}