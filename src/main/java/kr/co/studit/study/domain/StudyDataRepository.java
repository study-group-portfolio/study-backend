package kr.co.studit.study.domain;

import kr.co.studit.member.domain.Member;
import kr.co.studit.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface StudyDataRepository extends JpaRepository<Study, Long> {
    public List<Study> findStudyByMember(Member member);


}
