package tw.pago.pagobackend.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsCredentialsConfig {

    @Bean
    public AWSStaticCredentialsProvider awsStaticCredentialsProvider() {
        String accessKeyId = new ProfileCredentialsProvider("pagodev").getCredentials().getAWSAccessKeyId();
        String secretAccessKey = new ProfileCredentialsProvider("pagodev").getCredentials().getAWSSecretKey();
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        return new AWSStaticCredentialsProvider(awsCredentials);
    }
}
