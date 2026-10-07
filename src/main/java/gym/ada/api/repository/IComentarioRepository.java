package gym.ada.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gym.ada.api.model.Comentario;

import java.util.List;


@Repository
public interface IComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByActivoTrueOrderByFechaDesc();
}