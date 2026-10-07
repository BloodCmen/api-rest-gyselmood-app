package gym.ada.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gym.ada.api.model.ProductoTalla;


public interface IProductoTallaRepository extends JpaRepository <ProductoTalla, Long>  {

	
	 Optional<ProductoTalla> findByProductoIdAndTallaId(
	            Long productoId,
	            Long tallaId
	    );
}
