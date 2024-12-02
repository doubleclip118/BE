package PNUMEAT.Backend.global.security.filter;

import PNUMEAT.Backend.domain.auth.constant.AuthConstant;
import PNUMEAT.Backend.domain.auth.repository.RefreshTokenRepository;
import PNUMEAT.Backend.global.error.ErrorCode;
import PNUMEAT.Backend.global.security.utils.servletUtils.cookie.CookieUtils;
import PNUMEAT.Backend.global.security.utils.servletUtils.jwtUtils.FilterResponseUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

@AllArgsConstructor
public class JWTLogoutFilter extends GenericFilterBean {

    private final RefreshTokenRepository refreshRepository;
    private final FilterResponseUtils filterResponseUtils;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        if (!isUrlLogout(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!isHttpMethodPost(request.getMethod())) {
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

        refreshRepository.deleteByToken(refresh);

        CookieUtils.clearCookie(response);

        filterChain.doFilter(request, response);
    }

    private boolean isHttpMethodPost(String requestMethod) {
        return requestMethod.equals("POST");
    }

    private boolean isUrlLogout(String requestUri) {
        return requestUri.matches("^/api/v1/logout$");
    }

}