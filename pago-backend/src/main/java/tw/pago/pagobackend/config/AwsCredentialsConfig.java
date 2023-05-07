package tw.pago.pagobackend.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsCredentialsConfig {

    @Value("${aws.profile:}")
    private String awsProfile;

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        AWSCredentialsProvider credentialsProvider;

        if (awsProfile != null && !awsProfile.isEmpty()) {
            File credentialsFile = new File(System.getProperty("user.home") + "/.aws/credentials");
            if (credentialsFile.exists()) {
                ProfilesConfigFile profilesConfigFile = new ProfilesConfigFile(credentialsFile);
                if (profilesConfigFile.getAllBasicProfiles().containsKey(awsProfile)) {
                    try {
                        credentialsProvider = new ProfileCredentialsProvider(awsProfile);
                        credentialsProvider.getCredentials(); // Attempt to fetch credentials
                    } catch (Exception e) {
                        System.out.println("Failed to fetch credentials from specified profile: " + awsProfile + ". Falling back to DefaultAWSCredentialsProviderChain.");
                        credentialsProvider = new DefaultAWSCredentialsProviderChain();
                    }
                } else {
                    System.out.println("Specified AWS profile does not exist: " + awsProfile + ". Falling back to DefaultAWSCredentialsProviderChain.");
                    credentialsProvider = new DefaultAWSCredentialsProviderChain();
                }
            } else {
                System.out.println("AWS credentials file not found. Falling back to DefaultAWSCredentialsProviderChain.");
                credentialsProvider = new DefaultAWSCredentialsProviderChain();
            }
        } else {
            credentialsProvider = new DefaultAWSCredentialsProviderChain();
        }

        return credentialsProvider;
    }
}