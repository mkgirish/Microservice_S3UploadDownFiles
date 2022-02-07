package com.example.s3files.s3files;

import com.example.s3files.s3files.services.AWSS3StorageService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class AWSS3StorageServiceTest {

    @Bean
    @Primary
    public AWSS3StorageService AWSS3StorageService() {
        return Mockito.mock(AWSS3StorageService.class);
    }
}
