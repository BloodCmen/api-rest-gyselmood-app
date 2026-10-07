package gym.ada.api.service;

import java.util.List;

import gym.ada.api.model.Comentario;

public interface IComentarioService {

	public List<Comentario> listarActivos();

	public Comentario guardar(Comentario comentario);

	public Comentario buscarPorId(Long id);

	public void eliminar(Long id);

}