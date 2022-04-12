package kr.co.studit.entity.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.studit.entity.Skill;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "positions")
@Getter
@Setter
public class Position {
    // 분야 ID
    @Id
    @GeneratedValue
    @Column(name = "position_id")
    private Long id;

    private String positionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_type_id")
    private PositionType positionType;



    @JsonIgnore
    @OneToMany(mappedBy = "position",cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<>();

    public static Position createPostion(String positionName) {
        Position position = new Position();
        position.setPositionName(positionName);
        return position;
    }

    public void setPositionType(PositionType positionType) {
        this.positionType = positionType;
        positionType.getPosition().add(this);
    }
}
