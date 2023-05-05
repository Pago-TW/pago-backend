package tw.pago.pagobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import tw.pago.pagobackend.security.model.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class PagoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PagoBackendApplication.class, args);
    }

}
