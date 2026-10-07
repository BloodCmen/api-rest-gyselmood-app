package gym.ada.api.service;


import java.util.List;

import gym.ada.api.dto.CategoriaCreateDto;
import gym.ada.api.dto.CategoriaDto;


import gym.ada.api.model.Categoria;

public interface ICategoriaService {
	
	public List<Categoria> listarCategoria();

	public Categoria buscarCategoriaPorId(Long id);

	public Categoria guardarCategoria(Categoria categoria);

	public void eliminarCategoria(Long id);
	
	
	
	
	
	
	public List<CategoriaDto> vizualizarCategoria();
    public CategoriaDto crearCategoria(CategoriaCreateDto talla);
    public void quitarCategoria(Long id);

}
