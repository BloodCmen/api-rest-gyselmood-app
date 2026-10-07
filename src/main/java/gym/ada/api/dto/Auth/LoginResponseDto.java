package gym.ada.api.dto.Auth;

import gym.ada.api.dto.UsuarioDto;

public class LoginResponseDto {

    private String token;
    private UsuarioDto usuario;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, UsuarioDto usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }
}