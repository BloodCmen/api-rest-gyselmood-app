package gym.ada.api.service;

import java.util.List;

import gym.ada.api.dto.TallaCreateDto;
import gym.ada.api.dto.TallaListDto;
import gym.ada.api.model.Talla;

public interface ITallaService {

	
	//CRUD BASIC
    public List<Talla> listarTallas();

    public Talla buscarTallasPorId(Long id);

    public Talla guardarTallas(Talla talla);

    public void eliminarTallas(Long id);
    
    
    // IMPLEMENTACION DTOS
    
    
    public List<TallaListDto> vizualizarTallas();
    public Talla crearTalla(TallaCreateDto talla);
    public void eliminarTalla(Long id);

}