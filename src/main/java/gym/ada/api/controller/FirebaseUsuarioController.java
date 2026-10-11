package gym.ada.api.controller;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class FirebaseUsuarioController {

    @GetMapping("/listarFirebase")
    public ResponseEntity<?> listarUsuariosFirebase() {

        try {
            List<Map<String, Object>> usuarios = new ArrayList<>();

            ListUsersPage pagina =
                    FirebaseAuth.getInstance().listUsers(null);

            while (pagina != null) {
                for (UserRecord usuario : pagina.getValues()) {
                    Map<String, Object> datos = new HashMap<>();

                    datos.put("uid", usuario.getUid());
                    datos.put("email", usuario.getEmail());
                    datos.put("nombre", usuario.getDisplayName());
                    datos.put("emailVerificado", usuario.isEmailVerified());
                    datos.put("activo", !usuario.isDisabled());

                    usuarios.add(datos);
                }

                pagina = pagina.getNextPage();
            }

            return ResponseEntity.ok(usuarios);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "No se pudieron listar los usuarios de Firebase",
                            "detalle", e.getMessage()
                    ));
        }
    }
}