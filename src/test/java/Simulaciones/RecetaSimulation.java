package Simulaciones;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import org.springframework.beans.factory.annotation.Value;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class RecetaSimulation extends Simulation {

    @Value("${backUrl}")
    private String backUrl;

    // Configuración del protocolo HTTP
    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080") // Cambia según tu entorno
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Escenario: Crear una receta
    ScenarioBuilder crearReceta = scenario("Crear Receta")
            .exec(http("POST /crear")
                    .post("/crear")
                    .bodyPart(RawFileBodyPart("imagen", "src/test/resources/receta.jpg")) // Imagen como archivo
                    .bodyPart(StringBodyPart("receta", "{\"titulo\": \"Tarta de Manzana\", \"ingredientes\": \"Manzanas, Harina\"}")) // JSON como string
                    .asMultipartForm() // Especifica que el cuerpo es multipart/form-data
                    .check(status().is(200))
                    .check(jsonPath("$.mensaje").exists()) // Verifica que hay un mensaje en la respuesta
            );

    // Escenario: Mostrar recetas por categoría
    ScenarioBuilder mostrarRecetasPorCategoria = scenario("Mostrar Recetas por Categoría")
            .exec(http("GET /recetas")
                    .get("/recetas")
                    .queryParam("categoria", "Postres")
                    .queryParam("page", "0")
                    .queryParam("size", "6")
                    .check(status().is(200))
                    .check(jsonPath("$[0].titulo").exists())) // Verifica que la primera receta tiene un título
            .pause(2);

    // Escenario: Ver detalles de una receta
    ScenarioBuilder verReceta = scenario("Ver Receta")
            .exec(http("GET /{idReceta}")
                    .get("/1") // ID de receta como ejemplo
                    .check(status().is(200))
                    .check(jsonPath("$.titulo").is("Tarta de Manzana"))) // Verifica el título de la receta
            .pause(2);

    // Escenario: Obtener la imagen de una receta
    ScenarioBuilder obtenerImagenReceta = scenario("Obtener Imagen de Receta")
            .exec(http("GET /{idReceta}/imagen")
                    .get("/1/imagen")
                    .check(status().is(200))
                    .check(header("Content-Type").is("image/jpeg"))) // Verifica el tipo de contenido
            .pause(2);

    // Escenario: Obtener la foto del autor
    ScenarioBuilder obtenerFotoAutor = scenario("Obtener Foto del Autor")
            .exec(http("GET /{idReceta}/foto-autor")
                    .get("/1/foto-autor")
                    .check(status().is(200))
                    .check(header("Content-Type").is("image/jpeg"))) // Verifica el tipo de contenido
            .pause(2);

    {
        // Configuración de los escenarios
        setUp(
                crearReceta.injectOpen(rampUsers(5).during(10)), // 5 usuarios en 10 segundos
                mostrarRecetasPorCategoria.injectOpen(atOnceUsers(3)), // 3 usuarios al instante
                verReceta.injectOpen(constantUsersPerSec(2).during(20)), // 2 usuarios por segundo durante 20 segundos
                obtenerImagenReceta.injectOpen(atOnceUsers(3)), // 3 usuarios al instante
                obtenerFotoAutor.injectOpen(rampUsers(2).during(5)) // 2 usuarios en 5 segundos
        ).protocols(httpProtocol);
    }
}
