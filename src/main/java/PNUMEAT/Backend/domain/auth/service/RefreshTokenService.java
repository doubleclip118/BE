package PNUMEAT.Backend.domain.auth.service;

import PNUMEAT.Backend.domain.auth.entity.User;
import PNUMEAT.Backend.domain.auth.entity.RefreshToken;
import PNUMEAT.Backend.domain.auth.repository.UserRepository;
import PNUMEAT.Backend.domain.auth.repository.RefreshTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken renewalRefreshToken(String previousToken, String newToken, Long expiredMs) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(previousToken)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 REFRESH_TOKEN 입니다."));

        Date newExpireTime = new Date(System.currentTimeMillis() + expiredMs);

        refreshToken.updateToken(newToken, newExpireTime.toString());

        return refreshToken;
    }

    @Transactional
    public RefreshToken addRefreshEntity(String refresh, String uuid, Long expiredMs) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException(uuid + "의 회원이 없습니다."));

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshToken = new RefreshToken(user, refresh, date.toString());

        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public Optional<RefreshToken> findRefreshToken(Long memberId) {
        return refreshTokenRepository.findRefreshTokenByUserId(memberId);
    }

}
