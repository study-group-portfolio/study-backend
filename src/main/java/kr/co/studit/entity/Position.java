package kr.co.studit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Position {
    // 분야 ID
    @Id
    @GeneratedValue
    @Column(name = "position_id")
    private Long id;

    private String position;

    @JsonIgnore
    @OneToMany(mappedBy = "position")
    private List<Skill> skills = new ArrayList<>();
}
