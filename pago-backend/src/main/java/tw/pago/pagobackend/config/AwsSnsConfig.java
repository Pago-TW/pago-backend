package tw.pago.pagobackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;

@Configuration
public class AwsSnsConfig {
    
    @Autowired
    AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    public AmazonSNS amazonSNS() {
        return AmazonSNSClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(Regions.AP_NORTHEAST_1)
                .build();
    }
}
