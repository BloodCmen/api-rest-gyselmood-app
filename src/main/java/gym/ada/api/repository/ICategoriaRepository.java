package gym.ada.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gym.ada.api.model.Categoria;

@Repository
public interface ICategoriaRepository extends JpaRepository <Categoria, Long>{
	
	Optional<Categoria> findByNombre(String nombre);
	Optional<Categoria> findByCodigo(String codigo);
	
	//iria vacio aca no temrine de entender por completo XD

}
