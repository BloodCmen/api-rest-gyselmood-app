package gym.ada.api.dto.Auth;

import jakarta.validation.constraints.NotBlank;

public class GoogleLoginDto {

    @NotBlank
    private String token;

    public GoogleLoginDto() {
    }

    public GoogleLoginDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}