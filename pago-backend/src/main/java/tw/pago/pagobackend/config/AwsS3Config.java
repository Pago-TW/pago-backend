package tw.pago.pagobackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsS3Config {

    private String getAccessKeyId() {
        return new ProfileCredentialsProvider("pagodev").getCredentials().getAWSAccessKeyId();
    }

    private String getSecretAccessKey() {
        return new ProfileCredentialsProvider("pagodev").getCredentials().getAWSSecretKey();
    }

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(getAccessKeyId(), getSecretAccessKey());
        return AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(Regions.AP_NORTHEAST_1)
            .build();
    }
}

