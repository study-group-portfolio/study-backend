package kr.co.studit.entity.position;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class PositionType {
    @Id
    @GeneratedValue
    @Column(name = "position_type_id")
    private Long id;

    private String positionTypeName;

    @OneToMany(mappedBy = "positionType", cascade = CascadeType.ALL)
    private List<Position> position = new ArrayList<>();

    public static PositionType createPostionType(String positionTypeName) {
        PositionType positionType = new PositionType();
        positionType.setPositionTypeName(positionTypeName);
        return positionType;
    }

}
