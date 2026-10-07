package gym.ada.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import gym.ada.api.model.Producto;

@Repository
public interface IProductoRepository extends  JpaRepository <Producto, Long>{

	Optional<Producto> findByTitulo(String titulo);
	
	List<Producto> findByActivoTrue();
	
	List<Producto> findByCategoriaId(Long categoriaId);
	
	@Query("SELECT DISTINCT p FROM Producto p " +
		       "LEFT JOIN FETCH p.categoria " +
		       "LEFT JOIN FETCH p.imagenes " +
		       "LEFT JOIN FETCH p.productoTallas pt " +
		       "LEFT JOIN FETCH pt.talla")
		List<Producto> findAllConDetalles();
	
	
	
	@Query("""
		    SELECT DISTINCT p FROM Producto p
		    LEFT JOIN FETCH p.imagenes
		    LEFT JOIN FETCH p.productoTallas pt
		    LEFT JOIN FETCH pt.talla
		    LEFT JOIN FETCH p.categoria
		    WHERE p.activo = true
		    """)
		List<Producto> findByActivoTrueConDetalles();
}
