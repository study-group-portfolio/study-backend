package kr.co.studit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"area"}
                )
        }
)
public class Region {
    @Id
    @GeneratedValue
    @Column(name = "region_id")
    private Long id;
    private String area;

    @JsonIgnore
    @OneToMany(mappedBy = "region")
    List<Study> studies = new ArrayList<>();

    public static Region createRegion(String area) {
        Region region = new Region();
        region.area = area;
        return region;
    }
}
