package gym.ada.api.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseTokenService {

    public FirebaseToken verificarToken(String token) {

        try {
            return FirebaseAuth.getInstance()
                    .verifyIdToken(token);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Token de Firebase inválido"
            );
        }
    }

    public String obtenerUid(String token) {
        return verificarToken(token).getUid();
    }
}