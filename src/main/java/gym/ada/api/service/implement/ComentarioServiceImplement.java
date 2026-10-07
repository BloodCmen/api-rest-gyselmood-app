package gym.ada.api.service.implement;

import gym.ada.api.model.Comentario;
import gym.ada.api.repository.IComentarioRepository;
import gym.ada.api.service.ICategoriaService;
import gym.ada.api.service.IComentarioService;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComentarioServiceImplement implements IComentarioService {

    private final IComentarioRepository comentarioRepository;

    public ComentarioServiceImplement(IComentarioRepository comentarioRepository) {
        this.comentarioRepository = comentarioRepository;
    }

    public List<Comentario> listarActivos() {
        return comentarioRepository.findByActivoTrueOrderByFechaDesc();
    }

    public Comentario guardar(Comentario comentario) {
        return comentarioRepository.save(comentario);
    }

    public void eliminar(Long id) {
        comentarioRepository.deleteById(id);
    }
    public Comentario buscarPorId(Long id) {
        return comentarioRepository.findById(id).orElse(null);
    }
}