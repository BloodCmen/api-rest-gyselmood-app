package gym.ada.api.service.implement;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gym.ada.api.dto.CategoriaCreateDto;
import gym.ada.api.dto.CategoriaDto;
import gym.ada.api.model.Categoria;
import gym.ada.api.repository.ICategoriaRepository;
import gym.ada.api.service.ICategoriaService;
import jakarta.transaction.Transactional;

@Service
public class CategoriaServiceImplement implements ICategoriaService {
	
	private final  ICategoriaRepository categoriaRepository;
	
	 
	 
    public CategoriaServiceImplement(ICategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }
	
	
	///estrcutura basica//
	
	public List<Categoria> listarCategoria(){
		return categoriaRepository.findAll();
	}
	
	public Categoria buscarCategoriaPorId(Long id){
		return  categoriaRepository.findById(id).orElse(null);
	}
	
	public Categoria guardarCategoria(Categoria usuario){
		return  categoriaRepository.save(usuario);
	}
	
	public void eliminarCategoria(Long id) {
		categoriaRepository.deleteById(id);
	}


	@Override
	public List<CategoriaDto> vizualizarCategoria() {

	    List<Categoria> categorias = categoriaRepository.findAll();

	    return categorias.stream()
	            .map(this::convertirCategoriaDto)
	            .collect(Collectors.toList());
	}


	// =========================================================
	// CREAR CATEGORÍA
	// =========================================================

	@Override
	@Transactional
	public CategoriaDto crearCategoria(CategoriaCreateDto dto) {

	    Categoria categoria = new Categoria();

	    categoria.setCodigo(dto.getCodigo());
	    categoria.setNombre(dto.getNombre());

	    Categoria categoriaGuardada = categoriaRepository.save(categoria);

	    return convertirCategoriaDto(categoriaGuardada);
	}

	// =========================================================
	// CONVERTIR CATEGORÍA → DTO
	// =========================================================

	private CategoriaDto convertirCategoriaDto(Categoria categoria) {

	    CategoriaDto dto = new CategoriaDto();

	    dto.setId(categoria.getId());
	    dto.setCodigo(categoria.getCodigo());
	    dto.setNombre(categoria.getNombre());

	    return dto;
	}


	// =========================================================
	// CONVERTIR CATEGORÍA → CREATE DTO
	// =========================================================

	private CategoriaCreateDto convertirCategoriaCreateDto(Categoria categoria) {

	    CategoriaCreateDto dto = new CategoriaCreateDto();

	    dto.setCodigo(categoria.getCodigo());
	    dto.setNombre(categoria.getNombre());

	    return dto;
	}


	// =========================================================
	// ELIMINAR CATEGORÍA
	// =========================================================

	@Override
	@Transactional
	public void quitarCategoria(Long id) {

	    Categoria categoria = categoriaRepository.findById(id)
	            .orElseThrow(() ->
	                new RuntimeException(
	                    "Categoría no encontrada con el ID: " + id
	                )
	            );

	    categoriaRepository.delete(categoria);
	}

}
