package PNUMEAT.Backend.domain.auth.repository;


import PNUMEAT.Backend.domain.auth.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberUniqueId(String memberUniqueId);

    Optional<Member> findByEmail(String email);

    Optional<Member> findByUuid(String uuid);
}
