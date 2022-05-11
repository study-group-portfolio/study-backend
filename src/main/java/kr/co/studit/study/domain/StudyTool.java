package kr.co.studit.study.domain;

import kr.co.studit.entity.Tool;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StudyTool {

    @Id
    @GeneratedValue
    @Column(name = "study_tool_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name ="study_id")
    Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="tool_id")
    Tool tool;

    public static StudyTool createStudyTool(Study study, Tool tool) {
        StudyTool studyTool = new StudyTool();
        studyTool.setStudy(study);
        studyTool.setTool(tool);
        return studyTool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
        tool.getStudyTool().add(this);
    }

    public void setStudy(Study study) {
        this.study = study;
        study.getStudytool().add(this);
    }
}
