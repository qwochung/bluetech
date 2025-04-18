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


		// Lấy lại từ ENV nếu chạy trên Docker
		String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
		String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
		String jwt = System.getenv("JWT_SECRET");

		if (accessKey != null) System.setProperty("AWS_ACCESS_KEY_ID", accessKey);
		if (secretKey != null) System.setProperty("AWS_SECRET_ACCESS_KEY", secretKey);
		if (jwt != null) System.setProperty("JWT_SECRET", jwt);


		SpringApplication.run(BluetechApplication.class, args);
	}

}
