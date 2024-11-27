package PNUMEAT.Backend.global.images;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloud.aws.credentials")
@Getter
public class AwsProperties {

    private String accessKey;
    private String secretKey;
    private String region;

}
