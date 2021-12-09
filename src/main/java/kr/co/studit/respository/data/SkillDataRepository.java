package kr.co.studit.respository.data;

import kr.co.studit.entity.Position;
import kr.co.studit.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillDataRepository extends JpaRepository<Skill, Long> {
    public Skill findSkillBySkill(String skill);

}
