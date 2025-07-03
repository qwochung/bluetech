package com.example.bluetech;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BluetechApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
//        dotenv = Dotenv.configure().directory("/app").load();    // To build image local


		System.setProperty("AWS_ACCESS_KEY_ID", dotenv.get("AWS_ACCESS_KEY_ID"));
		System.setProperty("AWS_SECRET_ACCESS_KEY", dotenv.get("AWS_SECRET_ACCESS_KEY"));

		//		JWT Configuration
		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
		System.setProperty("JWT_RESET_EXPIRATION", dotenv.get("JWT_RESET_EXPIRATION"));

		System.setProperty("EMAIL_SENDER", dotenv.get("EMAIL_SENDER"));
		System.setProperty("EMAIL_SENDER_PASSWORD", dotenv.get("EMAIL_SENDER_PASSWORD"));

		System.setProperty("OPENAI_KEY", dotenv.get("OPENAI_KEY"));


		SpringApplication.run(BluetechApplication.class, args);
	}

}
