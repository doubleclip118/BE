package PNUMEAT.Backend.global.security.resolver;

import PNUMEAT.Backend.domain.auth.entity.User;
import PNUMEAT.Backend.domain.auth.repository.UserRepository;
import PNUMEAT.Backend.global.security.annotation.LoginMember;
import PNUMEAT.Backend.global.security.utils.jwt.JWTUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@AllArgsConstructor
@Slf4j
public class CustomAuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final JWTUtils jwtUtils;

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAuthenticationMember(authentication)) {
            log.info("Authentication 객체가 없거나, 익명 사용자 입니다.");
            return null;
        }

        User user = getMemberFromAuthentication(authentication);

        log.info("member name = {}", user.getUsername());

        return user;
    }

    private User getMemberFromAuthentication(Authentication authentication) {
        // jwt token 추출
        String token = (String) authentication.getPrincipal();

        String uuid = jwtUtils.getUUID(token);

        return userRepository.findByUuid(uuid).orElseThrow();
    }

    private boolean isAuthenticationMember(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}
