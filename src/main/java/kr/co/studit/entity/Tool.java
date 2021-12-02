package kr.co.studit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Tool {
    @Id
    @GeneratedValue
    @Column(name = "tool_id")
    private Long id;

    private String tool;
}
