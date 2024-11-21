package Simulaciones;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.springframework.beans.factory.annotation.Value;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class AutenticacionSimulation extends Simulation {

    @Value("${backUrl}")
    private String backUrl;

    // Configuración del protocolo HTTP
    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080") // Cambia la URL base según tu entorno
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Escenario: Registrar un usuario
    ScenarioBuilder registrarUsuario = scenario("Registrar Usuario")
            .exec(http("Registrar Usuario")
                    .post("/registrar")
                    .body(StringBody("{ \"username\": \"testuser\", \"password\": \"password123\", \"email\": \"test@example.com\" }")).asJson()
                    .check(status().is(200))) // Verifica que el registro fue exitoso
            .pause(2);

    // Escenario: Confirmar cuenta
    ScenarioBuilder confirmarCuenta = scenario("Confirmar Cuenta")
            .exec(http("Confirmar Cuenta")
                    .get("/confirmar?token=test-token")
                    .check(status().is(200))) // Verifica que la cuenta fue confirmada exitosamente
            .pause(2);

    // Escenario: Iniciar sesión
    ScenarioBuilder iniciarSesion = scenario("Iniciar Sesion")
            .exec(http("Iniciar Sesion")
                    .post("/login")
                    .body(StringBody("{ \"username\": \"testuser\", \"password\": \"password123\" }")).asJson()
                    .check(status().is(200))
                    .check(header("Set-Cookie").saveAs("jwtCookie"))) // Guarda la cookie JWT para usarla más adelante
            .pause(2);

    // Escenario: Verificar autenticación
    ScenarioBuilder verificarAutenticacion = scenario("Verificar Autenticacion")
            .exec(session -> session.set("jwtCookie", "JWT-TOKEN=test-jwt-token")) // Configura manualmente la cookie JWT
            .exec(http("Verificar Autenticacion")
                    .get("/verify-auth")
                    .header("Cookie", session -> session.getString("jwtCookie"))
                    .check(status().is(200))) // Verifica que la autenticación es válida
            .pause(2);

    {
        // Configurar la simulación con múltiples escenarios
        setUp(
                registrarUsuario.injectOpen(atOnceUsers(5)), // 5 usuarios al instante
                confirmarCuenta.injectOpen(rampUsers(10).during(10)), // 10 usuarios en 10 segundos
                iniciarSesion.injectOpen(constantUsersPerSec(5).during(20)), // 5 usuarios por segundo durante 20 segundos
                verificarAutenticacion.injectOpen(atOnceUsers(5)) // 5 usuarios verifican autenticación
        ).protocols(httpProtocol);
    }
}
