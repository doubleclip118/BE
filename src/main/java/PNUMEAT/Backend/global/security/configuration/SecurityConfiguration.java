package PNUMEAT.Backend.global.security.configuration;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import PNUMEAT.Backend.domain.auth.repository.RefreshTokenRepository;
import PNUMEAT.Backend.global.security.filter.JWTAccessFilter;
import PNUMEAT.Backend.global.security.filter.JWTLogoutFilter;
import PNUMEAT.Backend.global.security.filter.JWTRefreshFilter;
import PNUMEAT.Backend.global.security.oauth.OAuth2SuccessHandler;
import PNUMEAT.Backend.global.security.oauth.OAuth2UserServiceImpl;
import PNUMEAT.Backend.global.security.utils.jwt.JWTUtils;
import PNUMEAT.Backend.global.security.utils.servletUtils.jwtUtils.FilterResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final OAuth2UserServiceImpl oAuth2UserService;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    private final JWTUtils jwtUtil;

    private final RefreshTokenRepository refreshTokenRepository;

    private final FilterResponseUtils filterResponseUtils;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .addFilterAfter(new JWTAccessFilter(jwtUtil, filterResponseUtils),
                        OAuth2LoginAuthenticationFilter.class)
                .addFilterAfter(new JWTRefreshFilter(refreshTokenRepository, filterResponseUtils),
                        OAuth2LoginAuthenticationFilter.class)
                .addFilterBefore(new JWTLogoutFilter(refreshTokenRepository, filterResponseUtils), LogoutFilter.class);

        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(
                                (userInfoEndpointConfig) -> userInfoEndpointConfig.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/reissue").permitAll()
                        .anyRequest().authenticated());

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/favicon.ico")
                .requestMatchers("/articles/**")
                .requestMatchers("/error")
                .requestMatchers("/")
                .requestMatchers(toH2Console());
    }
}
