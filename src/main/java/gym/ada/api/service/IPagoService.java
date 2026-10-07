package gym.ada.api.service;

import gym.ada.api.dto.PagoResponseDto;
import gym.ada.api.dto.RegistrarPagoDto;


public interface IPagoService {

	PagoResponseDto  registrarPago( Long pedidoId, RegistrarPagoDto dto  );

	PagoResponseDto  obtenerPago(Long pedidoId);
}