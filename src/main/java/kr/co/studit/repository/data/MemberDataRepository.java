package kr.co.studit.repository.data;

import kr.co.studit.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface MemberDataRepository extends JpaRepository<Member, Long> {
    public Member findMemberByEmail(String email);
    public Boolean existsByEmail(String email);
}
