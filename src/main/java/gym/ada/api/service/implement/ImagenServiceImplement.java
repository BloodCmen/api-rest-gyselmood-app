package gym.ada.api.service.implement;

import java.util.List;

import org.springframework.stereotype.Service;

import gym.ada.api.model.Imagen;
import gym.ada.api.model.Usuario;
import gym.ada.api.repository.IImagenRepository;
import gym.ada.api.service.IImagenService;


@Service
public class ImagenServiceImplement implements IImagenService {
	
	
	private final IImagenRepository imagenRepository;

    public ImagenServiceImplement(IImagenRepository imagenRepository) {
        this.imagenRepository = imagenRepository;
    }
	
	
	///estrcutura basica//
	
	public List<Imagen> listarImagenes(){
		return imagenRepository.findAll();
	}
	
	public Imagen buscarImagenesPorId(Long id){
		return  imagenRepository.findById(id).orElse(null);
	}
	
	public Imagen guardarImagenes(Imagen imagen){
		return  imagenRepository.save(imagen);
	}
	
	public void eliminarImagenes(Long id) {
		imagenRepository.deleteById(id);
	}
	
	//UTIL FUTURO 
	public List<Imagen> buscarPorProducto(Long productoId) {
	    return imagenRepository.findByProductoId(productoId);
	}

	public Imagen buscarPrincipal(Long productoId) {
	    return imagenRepository.findByProductoIdAndEsPrincipalTrue(productoId).orElse(null);
	}

	
}
