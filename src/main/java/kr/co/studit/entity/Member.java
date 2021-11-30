package kr.co.studit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    // 읽기 전용
//    @JsonIgnore


    @OneToMany(mappedBy = "member")
    private List<Study> studys = new ArrayList<>();
}
