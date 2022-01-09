package kr.co.studit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Setter
@Getter
@Table(
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"email"}
                )
        }
)
@Builder @NoArgsConstructor @AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String password;
    // 읽기 전용
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private final List<Study> studys = new ArrayList<>();


    public static Member createMember(String email) {
        Member member = new Member();
        member.setEmail(email);
        return member;
    }
}
