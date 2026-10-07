package gym.ada.api.service.implement;

import gym.ada.api.dto.PagoResponseDto;
import gym.ada.api.dto.RegistrarPagoDto;
import gym.ada.api.enums.EstadoPago;
import gym.ada.api.enums.EstadoPedido;
import gym.ada.api.model.Pago;
import gym.ada.api.model.Pedido;
import gym.ada.api.repository.IPagoRepository;
import gym.ada.api.repository.IPedidoRepository;
import gym.ada.api.service.IPagoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PagoServiceImplement implements IPagoService {

    private final IPagoRepository pagoRepository;
    private final IPedidoRepository pedidoRepository;

    public PagoServiceImplement(
            IPagoRepository pagoRepository,
            IPedidoRepository pedidoRepository
    ) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional
    public PagoResponseDto registrarPago(
            Long pedidoId,
            RegistrarPagoDto dto
    ) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException("Pedido no encontrado")
                );

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new RuntimeException(
                    "No se puede pagar un pedido cancelado"
            );
        }

        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new RuntimeException(
                    "El pedido ya fue entregado"
            );
        }

        if (pagoRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new RuntimeException(
                    "El pedido ya tiene un pago registrado"
            );
        }

        Pago pago = new Pago();

        pago.setPedido(pedido);
        pago.setProveedor(dto.getProveedor());
        pago.setMetodo(dto.getMetodo());
        pago.setEstado(dto.getEstado());

        // El monto siempre sale del pedido
        pago.setMonto(pedido.getTotal());

        pago.setReferenciaExterna(
                dto.getReferenciaExterna()
        );

        if (dto.getEstado() == EstadoPago.APROBADO) {

            pago.setFechaPago(LocalDateTime.now());

            pedido.setEstado(EstadoPedido.PAGADO);

            pedidoRepository.save(pedido);
        }

        Pago pagoGuardado = pagoRepository.save(pago);

        return convertirAResponse(pagoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDto obtenerPago(Long pedidoId) {

        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "El pedido no tiene un pago registrado"
                        )
                );

        return convertirAResponse(pago);
    }

    private PagoResponseDto convertirAResponse(Pago pago) {

        PagoResponseDto dto = new PagoResponseDto();

        dto.setId(pago.getId());
        dto.setPedidoId(pago.getPedido().getId());
        dto.setProveedor(pago.getProveedor());
        dto.setMetodo(pago.getMetodo());
        dto.setEstado(pago.getEstado());
        dto.setMonto(pago.getMonto());
        dto.setReferenciaExterna(
                pago.getReferenciaExterna()
        );
        dto.setFechaPago(pago.getFechaPago());

        return dto;
    }
}

