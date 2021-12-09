package kr.co.studit.respository.data;

import kr.co.studit.entity.Member;
import kr.co.studit.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface MemberDataRepository extends JpaRepository<Member, Long> {
    public Member findMemberByEmail(String email);

}
