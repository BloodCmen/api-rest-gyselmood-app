package gym.ada.api.service;

import java.util.List;

import gym.ada.api.model.Imagen;

public interface IImagenService {

	public List<Imagen> listarImagenes();

	public Imagen buscarImagenesPorId(Long id);

	public Imagen guardarImagenes(Imagen imagen);

	public void eliminarImagenes(Long id);

	public List<Imagen> buscarPorProducto(Long productoId);

	public Imagen buscarPrincipal(Long productoId);

}