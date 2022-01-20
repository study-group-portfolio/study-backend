package kr.co.studit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Position {
    // 분야 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long id;

    private String positionName;

    @JsonIgnore
    @OneToMany(mappedBy = "position")
    private List<Skill> skills = new ArrayList<>();

    public static Position createPostion(String positionName) {
        Position position = new Position();
        position.setPositionName(positionName);
        return position;
    }
}
