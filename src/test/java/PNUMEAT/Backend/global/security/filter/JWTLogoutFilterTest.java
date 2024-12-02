package PNUMEAT.Backend.global.security.filter;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import PNUMEAT.Backend.domain.auth.constant.AuthConstant;
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
import jakarta.servlet.http.Cookie;
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
public class JWTLogoutFilterTest {

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
    @DisplayName("로그아웃 성공")
    void 로그아웃_성공() throws Exception {
        //given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, tokenRefresh);
        given(refreshTokenRepository.existsByToken(tokenRefresh)).willReturn(Boolean.TRUE);

        //expected
        mvc.perform(post("/api/v1/logout")
                        .header("Authorization","Bearer " + token)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃이 성공적으로 처리되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 시 쿠키에 Refresh Token이 없을 때 401 ERROR")
    void 로그아웃_시_쿠키에_REFRESH_TOKEN_없을_때_ERROR() throws Exception {
        //given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, tokenRefresh);

        //expected
        mvc.perform(post("/api/v1/logout")
                        .header("Authorization","Bearer " + token)
                        .cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_ERROR.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 시 Refresh Token이 DB에 없을 때 401 Error")
    void 로그아웃_시_REFRESH_TOKEN_DB에_없으면_ERROR() throws Exception {
        //given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, tokenRefresh);
        given(refreshTokenRepository.existsByToken(tokenRefresh)).willReturn(Boolean.FALSE);

        mvc.perform(post("/api/v1/logout")
                        .header("Authorization","Bearer " + token)
                        .cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_ERROR.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("Refresh Token이 만료 되었을 때 401 Error")
    void HEADER_TOKEN_만료_401_TEST(@Value("${spring.jwt.secret}") String secret) throws Exception {
        //given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, tokenRefresh);
        JWTUtils testJwt = new JWTUtils(secret);

        Field idField = JWTUtils.class.getDeclaredField("refreshExpiredTime");
        idField.setAccessible(true);
        idField.set(testJwt, 1L);

        tokenRefresh = testJwt.generateAccessToken("test", "ROLE_USER");

        //expected
        mvc.perform(post("/api/v1/logout")
                        .header("Authorization","Bearer " + token)
                        .cookie(cookie))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_ERROR.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 시 쿠키 Refresh Token이 Refresh Token이 아닐 시 Error")
    void 로그아웃_시_REFRESH_TOKEN_아닐_시_ERROR() throws Exception {
        //given
        Cookie cookie = new Cookie(AuthConstant.REFRESH_TOKEN, token);

        //expected
        mvc.perform(post("/api/v1/logout")
                        .header("Authorization","Bearer " + token)
                        .cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value(ErrorCode.TOKEN_ERROR.getMessage()))
                .andDo(print());
    }
}
