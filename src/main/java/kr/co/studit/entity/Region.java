package kr.co.studit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.studit.entity.member.MemberRegion;
import kr.co.studit.entity.study.Study;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;
    private String area;

    @JsonIgnore
    @OneToMany(mappedBy = "region")
    List<Study> studies = new ArrayList<>();

    @OneToMany(mappedBy = "region")
    private List<MemberRegion> members = new ArrayList<>();

    public static Region createRegion(String area) {
        Region region = new Region();
        region.area = area;
        return region;
    }

}
