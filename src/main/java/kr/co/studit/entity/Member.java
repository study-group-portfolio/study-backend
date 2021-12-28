package kr.co.studit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.studit.entity.enums.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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
    private String nickname;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean emailVerified;
    private String emaiCheckToken;
    private LocalDateTime emailCheckTokenGeneratedAt;
    // 읽기 전용
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private final List<Study> studys = new ArrayList<>();


    public static Member createMember(String email) {
        Member member = new Member();
        member.setEmail(email);
        return member;
    }

    public void generateEmailCheckToken() {
        this.emaiCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void compleateSignup() {
        this.emailVerified = true;
    }

    public boolean isVaildToken(String token) {
        return this.emaiCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }



}
