package PNUMEAT.Backend.global.security.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import PNUMEAT.Backend.domain.auth.controller.AuthApiController;
import PNUMEAT.Backend.domain.auth.repository.MemberRepository;
import PNUMEAT.Backend.domain.auth.repository.RefreshTokenRepository;
import PNUMEAT.Backend.domain.auth.service.RefreshTokenService;
import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.security.configuration.SecurityConfiguration;
import PNUMEAT.Backend.global.security.configuration.WebConfig;
import PNUMEAT.Backend.global.security.oauth.OAuth2SuccessHandler;
import PNUMEAT.Backend.global.security.oauth.OAuth2UserServiceImpl;
import PNUMEAT.Backend.global.security.utils.jwt.JWTUtils;
import PNUMEAT.Backend.global.security.utils.servletUtils.jwtUtils.FilterResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthApiController.class)
@Import({SecurityConfiguration.class, WebConfig.class})

public class JWTAccessFilterTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    OAuth2UserServiceImpl oAuth2UserService;

    @MockBean
    OAuth2SuccessHandler oAuth2SuccessHandler;

    @MockBean
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    ObjectMapper objectMapper;

    @SpyBean
    JWTUtils jwtUtils;

    @SpyBean
    FilterResponseUtils responseUtils;

    @MockBean
    WebSecurityCustomizer webSecurityCustomizer;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    RefreshTokenService refreshTokenService;

    String token;
    String tokenRefresh;

    @BeforeEach
    void setUp(){
        token = jwtUtils.generateAccessToken("test", "USER_ROLE");
        tokenRefresh = jwtUtils.generateRefreshToken("test", "USER_ROLE");
    }


    @Test
    @DisplayName("정상 통과")
    void 정상_통과() throws Exception {
        mvc.perform(get("/api/v1/auth")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인증된 회원입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("Header 에 Token 없을 시 401 Error")
    void HEADER_TOKEN_없을때_401_TEST() throws Exception {
        mvc.perform(get("/api/v1/auth"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.UNAUTHORIZED_USER_ERROR.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("Header 에 Token 값이 올바르지 않을 때 401 Error")
    void HEADER_TOKEN_값_이상_400_TEST() throws Exception {
        token = token + "test 용 이상한 값";

        mvc.perform(get("/api/v1/auth")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_SIGNATURE_ERROR.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("Header 에 Token 값이 만료 되었을 때 401 Error")
    void HEADER_TOKEN_만료_400_TEST(@Value("${spring.jwt.secret}") String secret) throws Exception {
        //given
        JWTUtils testJwt = new JWTUtils(secret);

        Field idField = JWTUtils.class.getDeclaredField("accessExpiredTime");
        idField.setAccessible(true);
        idField.set(testJwt, 1L);

        token = testJwt.generateAccessToken("test", "ROLE_USER");

        //expected
        mvc.perform(get("/api/v1/auth")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_EXPIRED_ERROR.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("Header에 Token이 AccessToken이 아닐 시 Error")
    void HEADER_TOKEN_ACCESS_TOKEN_아닐_시_ERROR() throws Exception {
        //expected
        mvc.perform(get("/api/v1/auth")
                        .header("Authorization","Bearer " + tokenRefresh))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_ERROR.getMessage()))
                .andDo(print());
    }
}
