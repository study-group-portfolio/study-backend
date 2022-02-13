package kr.co.studit.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.entity.position.Position;
import kr.co.studit.entity.Skill;
import kr.co.studit.entity.position.PositionType;
import kr.co.studit.entity.position.QPosition;
import kr.co.studit.entity.position.QPositionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.studit.entity.QSkill.skill;
import static kr.co.studit.entity.position.QPosition.position;
import static kr.co.studit.entity.position.QPositionType.positionType;

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
}
