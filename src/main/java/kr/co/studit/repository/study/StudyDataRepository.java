package kr.co.studit.repository.study;

import kr.co.studit.entity.member.Member;
import kr.co.studit.entity.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface StudyDataRepository extends JpaRepository<Study, Long> {
    public List<Study> findStudyByMember(Member member);


}
