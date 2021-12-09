package kr.co.studit.respository.data;

import kr.co.studit.entity.Member;
import kr.co.studit.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface StudyDataRepository extends JpaRepository<Study, Long> {
    public List<Study> findStudyByMember(Member member);


}
