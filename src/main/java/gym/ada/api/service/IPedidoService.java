package gym.ada.api.service;



import java.util.List;

import gym.ada.api.dto.CrearPedidoDto;
import gym.ada.api.dto.pedido.ActualizarEstadoPedidoDto;
import gym.ada.api.dto.pedido.HistorialEstadoResponseDto;
import gym.ada.api.dto.pedido.PedidoResponseDto;

public interface IPedidoService {

    PedidoResponseDto crearPedido(CrearPedidoDto dto);

    List<PedidoResponseDto> listarPedidosUsuario(String firebaseUid);

    PedidoResponseDto obtenerPedido(Long id, String firebaseUid);

    void cancelarPedido(Long id, String firebaseUid);
    
    void actualizarEstado(Long id, ActualizarEstadoPedidoDto dto);
    
    List<HistorialEstadoResponseDto> obtenerHistorial(Long id, String firebaseUid);
}