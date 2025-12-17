package com.nada.nada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 * Esta anotación combina @Configuration, @EnableAutoConfiguration y @ComponentScan,
 * permitiendo la configuración automática y el escaneo de componentes en el paquete base.
 */
@SpringBootApplication
public class NadaApplication {

	/**
	 * Punto de entrada de la aplicación.
	 * Inicia el contexto de Spring Boot y arranca el servidor embebido.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(NadaApplication.class, args);
	}

}
