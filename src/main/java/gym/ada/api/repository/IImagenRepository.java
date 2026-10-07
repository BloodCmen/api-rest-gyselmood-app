package gym.ada.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gym.ada.api.model.Imagen;

@Repository
public interface IImagenRepository extends JpaRepository <Imagen, Long> {

	
	// Buscar todas las imágenes de un producto
	List<Imagen> findByProductoId(Long productoId);

	// Buscar la imagen principal de un producto  
	Optional<Imagen> findByProductoIdAndEsPrincipalTrue(Long productoId);
}
