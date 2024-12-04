package PNUMEAT.Backend.domain.auth.controller;

import PNUMEAT.Backend.domain.auth.constant.AuthConstant;
import PNUMEAT.Backend.domain.auth.service.RefreshTokenService;
import PNUMEAT.Backend.global.error.dto.response.ApiResponse;
import PNUMEAT.Backend.global.security.utils.jwt.JWTUtils;
import PNUMEAT.Backend.global.security.utils.servletUtils.cookie.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthApiController {

    private final JWTUtils jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/auth")
    public ResponseEntity<ApiResponse<?>> authorization() {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.successResponseWithMessage("인증된 회원입니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(){
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.successResponseWithMessage("로그아웃이 성공적으로 처리되었습니다."));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<?>> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = (String) request.getAttribute(AuthConstant.REFRESH_TOKEN);

        Map<String, String> jwtInformation = jwtUtil.getJWTInformation(refresh);

        String uuid = jwtInformation.get(AuthConstant.UUID);
        String role = jwtInformation.get(AuthConstant.ROLE);

        String newAccess = jwtUtil.generateAccessToken(uuid, role);
        String newRefresh = jwtUtil.generateRefreshToken(uuid, role);

        refreshTokenService.renewalRefreshToken(refresh, newRefresh, jwtUtil.getRefreshExpiredTime());

        response.setHeader(AuthConstant.AUTHORIZATION, AuthConstant.BEARER + newAccess);
        response.addCookie(CookieUtils.createCookie(AuthConstant.ACCESS_TOKEN, newAccess));
        response.addCookie(CookieUtils.createCookie(AuthConstant.REFRESH_TOKEN, newRefresh));

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.successResponseWithMessage("토큰이 정상적으로 재발급 되었습니다."));
    }
}