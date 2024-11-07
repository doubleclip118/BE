package PNUMEAT.Backend.global.security.oauth;

import PNUMEAT.Backend.domain.auth.entity.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@AllArgsConstructor
@Getter
public class OAuth2UserImpl implements OAuth2User {

    private final User user;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return user.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }

    public String getUUID() {
        return user.getUuid();
    }
}
