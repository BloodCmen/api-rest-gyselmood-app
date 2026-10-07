package gym.ada.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gym.ada.api.model.Talla;

@Repository
public interface ITallaRepository extends JpaRepository <Talla, Long>{



	
	Optional<Talla> findByNombre(String nombre);
	
	
	//iria vacio aca no temrine de entender por completo XD

}
