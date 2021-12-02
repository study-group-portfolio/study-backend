package kr.co.studit.entity;

import javax.persistence.*;

@Entity
public class Skill {
    // 스킬 ID
    @Id
    @GeneratedValue
    @Column(name = "skill_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;
}
