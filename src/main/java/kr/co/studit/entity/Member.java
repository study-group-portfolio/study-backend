package kr.co.studit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String pwd;
    // 읽기 전용
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Study> studys = new ArrayList<>();

    //TODO 비밀번호 인코딩(o), JWT, react 연동, 외부로그인, responseDto error와 data 나눠서 줄 것(50%)
}
