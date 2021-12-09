package kr.co.studit.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Tool {
    @Id
    @GeneratedValue
    @Column(name = "tool_id")
    private Long id;

    private String tool;

    public static Tool createTool(String toolName) {
        Tool tool = new Tool();
        tool.setTool(toolName);
        return tool;
    }
}
