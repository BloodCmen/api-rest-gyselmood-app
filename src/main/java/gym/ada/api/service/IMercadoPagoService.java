
package gym.ada.api.service;

import gym.ada.api.dto.PagoResponseDto;
import gym.ada.api.dto.mercadopago.PreferenciaPagoResponseDto;

public interface IMercadoPagoService {

    PreferenciaPagoResponseDto crearPreferencia(Long pedidoId);

    PagoResponseDto verificarPago( Long pedidoId, String paymentId);
}
