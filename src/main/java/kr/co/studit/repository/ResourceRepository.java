package kr.co.studit.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.skill.Skill;
import kr.co.studit.position.domain.PositionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.studit.entity.QSkill.skill;
import static kr.co.studit.position.QPositionType.positionType;

@Repository
@RequiredArgsConstructor
public class ResourceRepository {
    private final JPAQueryFactory queryFactory;


    public List<PositionType> findPositionList() {
        return queryFactory
                .selectFrom(positionType)
                .fetch();
    }

    public List<Skill> findSkillByPositionName(String positionName) {
        return
                queryFactory
                        .selectFrom(skill)
                        .where(skill.position.positionName.eq(positionName))
                        .fetch();
    }

    public List<String> findAllSkills() {
        return
                queryFactory
                    .select(skill.skillName)
                    .from(skill)
                    .fetch();
    }


}
