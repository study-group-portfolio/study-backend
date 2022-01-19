package kr.co.studit.entity.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.studit.dto.ProfileForm;
import kr.co.studit.entity.study.Study;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.Role;
import kr.co.studit.entity.enums.StudyType;
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
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"email"}
                )
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    //프로필 공개 여부
    @Column(name = "public_profile")
    private boolean publicProfile;

    //자기소개
    private String bio;

    // 참여하고 싶은 스터디 유형
    @Enumerated(EnumType.STRING)
    private StudyType studyType;

    // 선호하는 진행 방식
    @Enumerated(EnumType.STRING)
    private OnOffStatus onOffStatus;

    @Builder.Default
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<MemberRegion> regions = new ArrayList<>();

    // 업무 포지션
    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<MemberPosition> positions = new ArrayList<>();

    //스킬
    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<MemberSkill> skills = new ArrayList<>();

    // 생성 날짜, 수정 날짜
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    // 이에일 인증 여부
    private boolean emailVerified;

    // 이메일 인증 토큰
    private String emaiCheckToken;

    // 이메일 인증 토큰 발급 시간
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

    public void createAt() {
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

    public void updateAt() {
        this.updateAt = LocalDateTime.now();
    }





    public void updateMember(ProfileForm profileForm) {
        this.bio = profileForm.getBio();
        this.studyType = profileForm.getStudyType();
        this.onOffStatus = profileForm.getOnOffStatus();

    }

    public void addRegions(MemberRegion memberRegion) {
        if (this.regions == null) {
            this.regions = new ArrayList<>();
        }
        this.regions.add(memberRegion);
    }

    public void addPositon(MemberPosition memberPosition) {
        if (this.positions == null) {
            this.positions = new ArrayList<>();
        }
        this.positions.add(memberPosition
        );
    }

    public void addSkill(MemberSkill memberSkill) {
        if (this.skills == null) {
            this.skills = new ArrayList<>();
        }
        this.skills.add(memberSkill);
    }

// TODO 프로필 포트폴리오 , 비밀번호 찾기, 비밀번호 재설정, 기타(스터디 활동)
}