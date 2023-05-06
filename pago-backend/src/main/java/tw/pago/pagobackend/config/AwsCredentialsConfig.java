package tw.pago.pagobackend.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsCredentialsConfig {

    @Value("${aws.profile:}")
    private String awsProfile;

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        if (awsProfile != null && !awsProfile.isEmpty()) {
            return new ProfileCredentialsProvider(awsProfile);
        } else {
            return new DefaultAWSCredentialsProviderChain();
        }
    }
}