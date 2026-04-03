package ar.uba.fi.ingsoft1.product_example;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class ProductExampleApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		String fromEnv = dotenv.get("APP_EXTERNAL_URL");
		if (fromEnv != null && !fromEnv.isBlank()) {
			System.setProperty("app.external-url", fromEnv);
		} else {
			System.setProperty("app.external-url", "http://localhost:5173");
		}

		SpringApplication.run(ProductExampleApplication.class, args);
	}
}
