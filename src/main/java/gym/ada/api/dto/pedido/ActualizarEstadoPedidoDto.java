package gym.ada.api.dto.pedido;

import gym.ada.api.enums.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public class ActualizarEstadoPedidoDto {

    @NotNull
    private EstadoPedido estado;

    private String comentario;

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}