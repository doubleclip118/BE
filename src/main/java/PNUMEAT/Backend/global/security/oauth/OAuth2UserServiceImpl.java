package PNUMEAT.Backend.global.security.oauth;

import static PNUMEAT.Backend.domain.auth.constant.AuthConstant.KAKAO;
import static PNUMEAT.Backend.domain.auth.constant.AuthConstant.NAVER;

import PNUMEAT.Backend.domain.auth.entity.User;
import PNUMEAT.Backend.domain.auth.repository.UserRepository;
import PNUMEAT.Backend.global.security.oauth.oauthResponse.KakaoResponse;
import PNUMEAT.Backend.global.security.oauth.oauthResponse.NaverResponse;
import PNUMEAT.Backend.global.security.oauth.oauthResponse.OAuth2Response;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if (registrationId.equals(NAVER)) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals(KAKAO)) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        Optional<User> memberOptional = userRepository.findByUsername(username);

        if (memberOptional.isPresent()) {
            User user = memberOptional.get();

            return new OAuth2UserImpl(user);
        }

        User user = new User(oAuth2Response.getEmail(), username, "ROLE_USER");

        userRepository.save(user);

        return new OAuth2UserImpl(user);
    }
}
