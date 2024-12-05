package ma.projet.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("ma.projet.grpc.entities")
public class Grpc2Application {

    public static void main(String[] args) {
        SpringApplication.run(Grpc2Application.class, args);
    }

}
