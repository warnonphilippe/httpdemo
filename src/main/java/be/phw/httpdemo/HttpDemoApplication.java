package be.phw.httpdemo;

import java.io.IOException;
import java.net.ServerSocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
 
@SpringBootApplication
public class HttpDemoApplication {

	// port to listen connection
	static final int PORT = 8081;

	// verbose mode
	static final boolean verbose = true;

	public static void main(String[] args) {
		SpringApplication.run(HttpDemoApplication.class, args);
		httpServer(args);
	}

	public static void httpServer(String[] args) {
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

			ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

			InfosService infosService = new InfosService();
			
			// we listen until user halts server execution
			while (true) {
				executor.execute(new JavaHTTPServer(serverConnect.accept(), infosService));
			}
			
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}

}
