package gym.ada.api.config;


import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void inicializarFirebase() {

        try {

            if (!FirebaseApp.getApps().isEmpty()) {
                return;
            }

            InputStream serviceAccount =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream(
                                    "firebase/gyselmoodapp-firebase-adminsdk-fbsvc-5dfd0a881c.json"
                            );

            if (serviceAccount == null) {
                throw new RuntimeException(
                        "No se encontró el archivo de credenciales de Firebase"
                );
            }

            FirebaseOptions options =
                    FirebaseOptions.builder()
                            .setCredentials(
                                    GoogleCredentials.fromStream(serviceAccount)
                            )
                            .build();

            FirebaseApp.initializeApp(options);

            System.out.println("Firebase Admin inicializado correctamente");

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al inicializar Firebase Admin",
                    e
            );
        }
    }
}