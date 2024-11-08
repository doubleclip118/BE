package PNUMEAT.Backend.domain.auth.controller;

import PNUMEAT.Backend.domain.auth.constant.AuthConstant;
import PNUMEAT.Backend.domain.auth.service.RefreshTokenService;
import PNUMEAT.Backend.global.security.utils.jwt.JWTUtils;
import PNUMEAT.Backend.global.security.utils.servletUtils.cookie.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@AllArgsConstructor
public class ReissueController {

    private final JWTUtils jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = (String) request.getAttribute(AuthConstant.REFRESH_TOKEN);

        Map<String, String> jwtInformation = jwtUtil.getJWTInformation(refresh);

        String uuid = jwtInformation.get(AuthConstant.UUID);
        String role = jwtInformation.get(AuthConstant.ROLE);

        String newAccess = jwtUtil.generateAccessToken(uuid, role);
        String newRefresh = jwtUtil.generateRefreshToken(uuid, role);

        refreshTokenService.renewalRefreshToken(refresh, newRefresh, jwtUtil.getRefreshExpiredTime());

        response.setHeader(AuthConstant.AUTHORIZATION, AuthConstant.BEARER + newAccess);

        response.addCookie(CookieUtils.createCookie(AuthConstant.REFRESH_TOKEN, newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}