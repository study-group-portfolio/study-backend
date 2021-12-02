package kr.co.studit.entity;

import javax.persistence.*;

@Entity
public class StudyTool {

    @Id
    @GeneratedValue
    @Column(name = "study_tool_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="study_id")
    Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    Tool tool;
}
