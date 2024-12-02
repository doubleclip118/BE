package PNUMEAT.Backend.global.security.filter;

import PNUMEAT.Backend.domain.auth.constant.AuthConstant;
import PNUMEAT.Backend.domain.auth.repository.RefreshTokenRepository;
import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.security.utils.servletUtils.cookie.CookieUtils;
import PNUMEAT.Backend.global.security.utils.servletUtils.jwtUtils.FilterResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;


@RequiredArgsConstructor
@Slf4j
public class JWTRefreshFilter extends OncePerRequestFilter {

    private final RefreshTokenRepository refreshRepository;
    private final FilterResponseUtils filterResponseUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!isUrlRefresh(request.getRequestURI())) {

            filterChain.doFilter(request, response);
            return;
        }

        String refresh = CookieUtils.checkRefreshTokenInCookie(request);

        if (refresh == null) {
            filterResponseUtils.generateTokenErrorResponse(ErrorCode.TOKEN_ERROR, response);
            return;
        }

        if (!filterResponseUtils.isTokenInDB(response, refresh)) {
            return;
        }

        if (filterResponseUtils.isTokenExpired(response, refresh)) {
            refreshRepository.deleteByToken(refresh);
            return;
        }

        if (!filterResponseUtils.checkTokenType(response, refresh, AuthConstant.REFRESH_TOKEN)) {
            return;
        }

        request.setAttribute(AuthConstant.REFRESH_TOKEN, refresh);

        filterChain.doFilter(request, response);
    }

    private boolean isUrlRefresh(String requestUri) {
        return requestUri.matches("^/api/v1/reissue(?:/.*)?$");
    }

}
