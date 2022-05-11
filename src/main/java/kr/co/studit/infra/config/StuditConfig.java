package kr.co.studit.infra.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StuditConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
