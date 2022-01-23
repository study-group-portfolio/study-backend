package kr.co.studit.config;


import kr.co.studit.filter.JwtAuthenticationFilter;
import kr.co.studit.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(0)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 어떠한 요청에도 인증 받게
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .httpBasic()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
       /*         .and()
                .authorizeRequests()
                .anyRequest().permitAll();*/

        http.authorizeRequests().antMatchers("/api/member/signin", "/api/auth/refresh-token").permitAll();
        http.authorizeRequests().antMatchers("/api/member/**").hasAnyAuthority("ROLE_USER");
        http.authorizeRequests().anyRequest().authenticated();


        /**
         filter 등록
         매 요청 마다
         UsernamePasswordAuthenticationFilter 실행전
         JwtAthenticationFilter 실행
          */

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
    };

}
