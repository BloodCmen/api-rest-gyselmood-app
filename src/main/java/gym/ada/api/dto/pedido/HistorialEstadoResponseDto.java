package gym.ada.api.dto.pedido;

import gym.ada.api.enums.EstadoPedido;

import java.time.LocalDateTime;

public class HistorialEstadoResponseDto {

    private EstadoPedido estado;
    private LocalDateTime fecha;
    private String comentario;

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}