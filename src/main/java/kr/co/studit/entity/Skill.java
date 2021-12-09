package kr.co.studit.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Skill {

    // 스킬 ID
    @Id
    @GeneratedValue
    @Column(name = "skill_id")
    private Long id;


    private String skillName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    public static Skill createSkill(String skillName) {
        Skill skill = new Skill();
        skill.setSkillName(skillName);
        return skill;
    }

    public void setPosition(Position position) {
        this.position = position;
        position.getSkills().add(this);
    }
}
