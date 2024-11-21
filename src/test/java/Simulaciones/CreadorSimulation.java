package Simulaciones;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.springframework.beans.factory.annotation.Value;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CreadorSimulation extends Simulation {

    @Value("${backUrl}")
    private String backUrl;

    // Configuración del protocolo HTTP
    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080") // Cambia según tu entorno
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Escenario: Obtener la cantidad de recetas creadas
    ScenarioBuilder obtenerCantidadRecetas = scenario("Obtener Cantidad de Recetas")
            .exec(http("GET /cantidadRecetas")
                    .get("/cantidadRecetas")
                    .check(status().is(200))
                    .check(jsonPath("$").find().saveAs("cantidadRecetas")) // Guarda el valor en la variable
            )
            .pause(2); // Pausa fuera del bloque .exec

    // Escenario: Ver el perfil del creador
    ScenarioBuilder verPerfil = scenario("Ver Perfil")
            .exec(http("GET /perfil")
                    .get("/perfil")
                    .check(status().is(200))
                    .check(jsonPath("$.nombre").exists()) // Verifica que el perfil contiene el campo "nombre"
            )
            .pause(2); // Pausa fuera del bloque .exec

    // Escenario: Actualizar la foto de perfil
    ScenarioBuilder actualizarFotoPerfil = scenario("Actualizar Foto de Perfil")
            .exec(http("POST /actualizarFotoPerfil")
                    .post("/actualizarFotoPerfil")
                    .formUpload("fotoPerfil", "src/test/resources/test-image.jpg") // Sube una imagen de prueba
                    .check(status().is(200))
                    .check(jsonPath("$.mensaje").exists()) // Verifica que la respuesta contiene un mensaje
            )
            .pause(2); // Pausa fuera del bloque .exec

    // Escenario: Obtener la foto de perfil
    ScenarioBuilder obtenerFotoPerfil = scenario("Obtener Foto de Perfil")
            .exec(http("GET /FotoPerfil")
                    .get("/FotoPerfil")
                    .check(status().is(200))
                    .check(header("Content-Type").is("image/jpeg")) // Verifica el tipo de contenido
            )
            .pause(2); // Pausa fuera del bloque .exec

    {
        // Configuración de los escenarios
        setUp(
                obtenerCantidadRecetas.injectOpen(atOnceUsers(5)), // 5 usuarios al instante
                verPerfil.injectOpen(rampUsers(10).during(10)), // 10 usuarios gradualmente en 10 segundos
                actualizarFotoPerfil.injectOpen(constantUsersPerSec(2).during(15)), // 2 usuarios por segundo durante 15 segundos
                obtenerFotoPerfil.injectOpen(atOnceUsers(3)) // 3 usuarios al instante
        ).protocols(httpProtocol);
    }
}
