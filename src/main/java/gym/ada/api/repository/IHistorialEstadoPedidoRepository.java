package gym.ada.api.repository;

import gym.ada.api.model.HistorialEstadoPedido;
import gym.ada.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IHistorialEstadoPedidoRepository
        extends JpaRepository<HistorialEstadoPedido, Long> {

    List<HistorialEstadoPedido> findByPedidoOrderByFechaAsc(Pedido pedido);
}