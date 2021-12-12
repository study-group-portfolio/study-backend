package kr.co.studit.repository.data;

import kr.co.studit.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDataRepository extends JpaRepository<Member, Long> {

    Member findMemberByEmail(String email);
    Boolean existsByEmail(String email);
}
