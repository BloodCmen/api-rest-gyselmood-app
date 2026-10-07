package gym.ada.api.repository;

import gym.ada.api.model.Pago;
import gym.ada.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPedido(Pedido pedido);
    

    Optional<Pago> findByPedidoId(Long pedidoId);

    Optional<Pago> findByReferenciaExterna(String referenciaExterna); //para pasarella de pago
}