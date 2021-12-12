package kr.co.studit.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Tool {
    @Id
    @GeneratedValue
    @Column(name = "tool_id")
    private Long id;

    private String toolName;

    @OneToMany(mappedBy = "tool")
    private List<StudyTool> studyTool = new ArrayList<StudyTool>();

    public static Tool createTool(String toolName) {
        Tool tool = new Tool();
        tool.setToolName(toolName);
        return tool;
    }
}
