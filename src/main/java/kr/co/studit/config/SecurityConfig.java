package kr.co.studit.config;


import kr.co.studit.filter.JwtAuthenticationFilter;
import kr.co.studit.filter.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(0)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private static final String[] AUTH_WHITELIST = {
            "/api/member/signin",
            "/api/member/signup",
            "/api/member/password/send-mail",
            "/api/member/checkNickname/*",
            "/api/member/profile/*",
            "/api/member/search",
            "/api/member/password",
            "/api/member/checkEmailToken/**",
            "/api/auth/refresh-token",
            "/api/member/checkFindPasswordToken/**",

            "/api/study/search"


    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


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
        http.authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/member/profile/*").permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/study").permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/study/*").permitAll();

        http.authorizeRequests().antMatchers("/api/member/**").hasAnyAuthority("ROLE_USER");
        http.authorizeRequests().antMatchers("/api/study/**").hasAnyAuthority("ROLE_USER");
        http.authorizeRequests().antMatchers("/api/bookmark/**").hasAnyAuthority("ROLE_USER");

        http.authorizeRequests().anyRequest().authenticated();


        /**
         filter 등록
         매 요청 마다
         UsernamePasswordAuthenticationFilter 실행전
         JwtAthenticationFilter 실행
         */

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
    }



    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/v2/api-docs",
                "/swagger-resources/**",
                "/configuration/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/swagger-ui/**");
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER > ROLE_GUEST");
        return roleHierarchy;
    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {
        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        return roleHierarchyVoter;
    }
}
