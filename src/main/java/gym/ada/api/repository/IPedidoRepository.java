package gym.ada.api.repository;

import gym.ada.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IPedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByFirebaseUidOrderByFechaCreacionDesc(String firebaseUid);

    Optional<Pedido> findByIdAndFirebaseUid(Long id, String firebaseUid);
}