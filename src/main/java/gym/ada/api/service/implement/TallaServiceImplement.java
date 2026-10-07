package gym.ada.api.service.implement;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gym.ada.api.dto.TallaCreateDto;
import gym.ada.api.dto.TallaListDto;
import gym.ada.api.model.Talla;
import gym.ada.api.repository.ITallaRepository;
import gym.ada.api.service.ITallaService;
import jakarta.transaction.Transactional;


@Service
public class TallaServiceImplement  implements ITallaService{

	private  final ITallaRepository tallaRepository;
		
    public TallaServiceImplement(ITallaRepository tallaRepository) {
        this.tallaRepository = tallaRepository;
    }
	
	///estrcutura basica//
	
	public List<Talla> listarTallas(){
		return tallaRepository.findAll();
	}
	
	public Talla buscarTallasPorId(Long id){
		return  tallaRepository.findById(id).orElse(null);
	}
	
	public Talla guardarTallas(Talla talla){
		return  tallaRepository.save(talla);
	}
	
	public void eliminarTallas(Long id) {
		tallaRepository.deleteById(id);
	}
	
	
	
	

	// =========================================================
	// LISTAR TALLAS
	// =========================================================
	@Override
	public List<TallaListDto> vizualizarTallas() {
	    List<Talla> tallas = tallaRepository.findAll();
	    return tallas.stream()
	            .map(this::convertirTallaDto)
	            .collect(Collectors.toList());
	}
	// =========================================================
	// CREAR TALLA
	// =========================================================
	@Override
	@Transactional
	public Talla crearTalla(TallaCreateDto dto) {
	    Talla talla = new Talla();
	    talla.setNombre(dto.getNombre()); // Ajusta si la entidad usa otro campo (ej. getTalla())
	    
	    return tallaRepository.save(talla);
	}
	
	// =========================================================
	// CONVERTIR TALLA -> DTO
	// =========================================================
	private TallaListDto convertirTallaDto(Talla talla) {
	    TallaListDto dto = new TallaListDto();
	    dto.setId(talla.getId());
	    dto.setNombre(talla.getNombre());
	    return dto;
	}
	
	@Override
	@Transactional
	public void eliminarTalla(Long id) {
	    Talla talla = tallaRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Talla no encontrada con el ID: " + id));
	            
	    tallaRepository.delete(talla);
	}
}
