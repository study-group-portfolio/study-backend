package kr.co.studit.repository;

import kr.co.studit.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);

    Boolean existsByEmail(String email);
}
