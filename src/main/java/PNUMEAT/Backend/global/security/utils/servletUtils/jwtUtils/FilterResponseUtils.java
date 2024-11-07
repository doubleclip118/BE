package PNUMEAT.Backend.global.security.utils.servletUtils.jwtUtils;

import PNUMEAT.Backend.domain.auth.repository.RefreshTokenRepository;
import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.error.dto.response.ApiResponse;
import PNUMEAT.Backend.global.error.response.ErrorResult;
import PNUMEAT.Backend.global.security.utils.jwt.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class FilterResponseUtils {

    private final JWTUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    public boolean isTokenExpired(HttpServletResponse response, String token) throws IOException {
        try {
            jwtUtils.isExpired(token);
        } catch (ExpiredJwtException e) {
            generateUnauthorizedErrorResponse(ErrorCode.TOKEN_EXPIRED_ERROR, response);
            return true;
        } catch (MalformedJwtException e) {
            generateUnauthorizedErrorResponse(ErrorCode.TOKEN_MALFORMED_ERROR, response);
            return true;
        } catch (UnsupportedJwtException e) {
            generateUnauthorizedErrorResponse(ErrorCode.TOKEN_UNSUPPORTED_ERROR, response);
            return true;
        } catch (SignatureException e) {
            generateUnauthorizedErrorResponse(ErrorCode.TOKEN_SIGNATURE_ERROR, response);
            return true;
        } catch (Exception e) {
            generateUnauthorizedErrorResponse(ErrorCode.TOKEN_ERROR, response);
            return true;
        }
        return false;
    }

    public boolean checkTokenType(HttpServletResponse response, String token, String type) {
        String category = jwtUtils.getCategory(token);

        if (!category.equals(type)) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }

    public void generateUnauthorizedErrorResponse(ErrorCode errorCode, HttpServletResponse response) throws IOException {
        ErrorResult errorResult = new ErrorResult(errorCode.getStatus().value(), errorCode.getMessage());

        String errorResponse = objectMapper.writeValueAsString(ApiResponse.errorResponse(errorResult));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorResponse);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    public void generateLogoutResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiResponse<?> loginApiResponse = new ApiResponse<>("success", 200, "로그아웃 처리가 완료 되었습니다.", null);

        String loginResponse = objectMapper.writeValueAsString(loginApiResponse);

        response.getWriter().write(loginResponse);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public boolean isTokenInDB(HttpServletResponse response, String token) {
        Boolean isExist = refreshTokenRepository.existsByToken(token);
        if (!isExist) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }
        return true;
    }

}
