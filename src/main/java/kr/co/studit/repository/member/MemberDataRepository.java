package kr.co.studit.repository.member;

import kr.co.studit.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
/**
 *  실제 사용하는 리포지토리
 * 단순한 CRUD 쿼리는 여기서 작성
 */
public interface MemberDataRepository extends JpaRepository<Member, Long>, MemberCustomRepository {
    Member findMemberByEmail(String email);
    Member findMemberById(Long id);
    Boolean existsByEmail(String email);
    Member findMemberByNickname(String nickname);
    Boolean existsByNickname(String nickname);


}
