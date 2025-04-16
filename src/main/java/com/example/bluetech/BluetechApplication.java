package com.example.bluetech;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BluetechApplication {

	public static void main(String[] args) {
//		Dotenv dotenv = Dotenv.load();
//		Dotenv dotenv = Dotenv.configure().directory("/app").load();
//		System.setProperty("AWS_ACCESS_KEY_ID", dotenv.get("AWS_ACCESS_KEY_ID"));
//		System.setProperty("AWS_SECRET_ACCESS_KEY", dotenv.get("AWS_SECRET_ACCESS_KEY"));



		System.setProperty("AWS_ACCESS_KEY_ID", "AWS_ACCESS_KEY_ID");
		System.setProperty("AWS_SECRET_ACCESS_KEY", "AWS_SECRET_ACCESS_KEY");


		SpringApplication.run(BluetechApplication.class, args);
	}

}
