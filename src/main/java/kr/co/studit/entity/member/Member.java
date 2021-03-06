package kr.co.studit.entity.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.studit.entity.Portfolio;
import kr.co.studit.entity.study.Study;
import kr.co.studit.dto.member.ProfileForm;
import kr.co.studit.entity.common.BaseTimeEntity;
import kr.co.studit.entity.Bookmark;
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
//        uniqueConstraints = {
//                @UniqueConstraint(
//                        columnNames = {"email","nickname"}
//                )
//        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "nickname", unique = true)
    private String nickname;
    @Column(name = "email", unique = true)
    private String email;
    private String password;
    private String profileImg;
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
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberRegion> regions = new ArrayList<>();

    // 업무 포지션
    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberPosition> positions = new ArrayList<>();

    //스킬
    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberSkill> skills = new ArrayList<>();

    // 이에일 인증 여부
    private boolean emailVerified;

    // 이메일 인증 토큰
    private String emaiCheckToken;

    // 이메일 인증 토큰 발급 시간
    private LocalDateTime emailCheckTokenGeneratedAt;

    // 비밀 번호 찾기 이메일 인증 토큰
    private String passwordFindToken;

    // 비밀 번호 찾기 이메일 토큰 발급 시간
    private LocalDateTime passwordFindTokenGeneratedAt;
    // 읽기 전용
    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private final List<Study> studys = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Portfolio> portfolios = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "markMember",
    cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarks = new ArrayList<>();

    public static Member createMember(String email) {
        Member member = new Member();
        member.setEmail(email);
        return member;
    }

    public void generateEmailCheckToken() {
        this.emaiCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void generatePasswordFindToken() {
        this.passwordFindToken = UUID.randomUUID().toString();
        this.passwordFindTokenGeneratedAt = LocalDateTime.now();
    }

    public void compleateSignup() {
        this.emailVerified = true;
    }

    public boolean isVaildEmailCheckToken(String token) {
        return this.emaiCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    public boolean isValidPasswordToken(String token) {
        if (this.getPasswordFindTokenGeneratedAt().isBefore(LocalDateTime.now().minusHours(12))) {
            return false;
        }
        return this.passwordFindToken.equals(token);
    }

    public Boolean canSendPasswordFindEmail() {
        return this.passwordFindTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(10));
    }

    public void updateMember(ProfileForm profileForm) {
        this.bio = profileForm.getBio();
        this.studyType = profileForm.getStudyType();
        this.onOffStatus = profileForm.getOnOffStatus();
        this.publicProfile = isPublicProfile();

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

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}