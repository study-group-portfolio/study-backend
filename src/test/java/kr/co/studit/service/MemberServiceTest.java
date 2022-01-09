package kr.co.studit.service;

import kr.co.studit.dto.ProfileForm;
import kr.co.studit.dto.SearchMemberDto;
import kr.co.studit.dto.SigninDto;
import kr.co.studit.dto.SignupDto;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import kr.co.studit.entity.member.Member;
import kr.co.studit.repository.RegionDataRepository;
import kr.co.studit.repository.data.MemberRegionDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Commit
class MemberServiceTest {

    static final String EMAIL = "studit@studit.com";

    @Autowired
    MemberService memberService;
    @Autowired
    MemberDataRepository memberDataRepository;

    @Autowired
    MemberRegionDataRepository memberRegionDataRepository;

    @Autowired
    RegionDataRepository regionDataRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    @BeforeEach
    void beforeEach() {
        SignupDto signupDto = new SignupDto();
        signupDto.setNickname("스터디왕");
        signupDto.setEmail(EMAIL);
        signupDto.setPassword("1234567");
        Member member = memberService.createMember(signupDto);
        memberService.compleateSignup(member);

        ProfileForm profileForm = new ProfileForm();
        profileForm.setNickname("스터디왕");
        profileForm.setBio("안녕하세요 백엔드 개발자 스터디왕 입니다");
        profileForm.setStudyType(StudyType.SHARE);
        profileForm.setOnOffStatus(OnOffStatus.ON);
        List<String> regions = new ArrayList<>();
        regions.add("서울");
        regions.add("용인");
        List<String> positions = new ArrayList<>();
        positions.add("백엔드");
        positions.add("기획");
        List<String> skills = new ArrayList<>();
        skills.add("자바");
        skills.add("스프링 부트");
        skills.add("JPA");


    }


    @DisplayName("회원 가입 테스트")
    @Test
    public void signup() throws Exception {
        //given
        SignupDto signupDto = new SignupDto();
        signupDto.setNickname("스터디왕");
        signupDto.setEmail(EMAIL);
        signupDto.setPassword("1234567");
        //when
        memberService.createMember(signupDto);
        Member newMember = memberDataRepository.findMemberByEmail(signupDto.getEmail());
        //then
        assertThat(newMember.getEmail()).isEqualTo(signupDto.getEmail());
        assertThat(newMember.getPassword()).isNotEqualTo(signupDto.getPassword());
    }

    @DisplayName("로그인 테스트")
    @Test
    public void signin() throws Exception {
        //given
        SigninDto signinDto = new SigninDto();
        signinDto.setEmail(EMAIL);
        signinDto.setPassword("1234567");
        //when

        Member findMember = memberService.signin(signinDto);
        //then
        assertThat(passwordEncoder.matches(signinDto.getPassword(), findMember.getPassword())).isTrue();
    }

    @Test
    @DisplayName("프로필 공개 여부 수정")
    public void editPuplicProfile() throws Exception {
        //given
        Member member = memberDataRepository.findMemberByEmail(EMAIL);
        ProfileForm profileForm = new ProfileForm();
        //when
        memberService.editProfile(profileForm, member.getNickname());
        member.setPublicProfile(true);
        member.updateAt();
        //then
        Member findMember = memberDataRepository.findMemberByEmail(EMAIL);
        assertThat(findMember.isPublicProfile()).isTrue();
        assertThat(findMember.getCreateAt()).isNotEqualTo(findMember.getUpdateAt());


    }

    @Test
    @DisplayName("프로필 활동 지역 수정")
    public void updateMemberRegion() throws Exception {
        //given
        Member member = memberDataRepository.findMemberByNickname("스터디왕");


        ProfileForm profileForm = new ProfileForm();
        profileForm.setNickname("스터디왕");
        profileForm.setBio("안녕하세요 백엔드 개발자 스터디왕 입니다");
        profileForm.setStudyType(StudyType.SHARE);
        profileForm.setOnOffStatus(OnOffStatus.ON);
        List<String> regions = new ArrayList<>();
        regions.add("서울");
        profileForm.setRegions(regions);
        List<String> positions = new ArrayList<>();
        positions.add("백엔드");
        profileForm.setPositions(positions);
        List<String> skills = new ArrayList<>();
        skills.add("스프링");
        profileForm.setSkills(skills);


        //when
//        Region pusan = regionDataRepository.findRegionByArea("부산");
//        Region seoul = regionDataRepository.findRegionByArea("서울");
//        List<MemberRegion> memberRegions = new ArrayList<>();
//        MemberRegion memberRegion1 = MemberRegion.createMemberRegion(member, pusan);
//        MemberRegion memberRegion2 = MemberRegion.createMemberRegion(member, seoul);

//
//        MemberRegion save1 = memberRegionDataRepository.save(memberRegion1);
//        MemberRegion save2 = memberRegionDataRepository.save(memberRegion2);
//        member.addMemberRegion(save1);
//        member.addMemberRegion(save2);
//        for (MemberRegion memberRegion: member.getMemberRegions()
//             ) {
//            System.out.println("memberRegion.getRegion().getArea() = " + memberRegion.getRegion().getArea());
//
//        }
        memberService.updateMemberRegion(profileForm.getRegions(), member);
        //then
        Member findMember = memberDataRepository.findMemberByNickname("스터디왕");

        assertThat(findMember.getRegions().size()).isEqualTo(2);
    }


//    @Test
    @DisplayName("프로필 업데이트")
    public void editProfile() throws Exception {
        //given

        //when

        //then
    }

    @Test
    public void findProfileFormByMemberId() throws Exception {
        //given
        Member member = memberDataRepository.findMemberByNickname("스터디왕");

        ProfileForm profileForm = new ProfileForm();
        profileForm.setNickname("스터디왕");
        profileForm.setBio("안녕하세요 백엔드 개발자 스터디왕 입니다");
        profileForm.setStudyType(StudyType.SHARE);
        profileForm.setOnOffStatus(OnOffStatus.ON);
        List<String> regions = new ArrayList<>();
        regions.add("서울");
        regions.add("부산");
        profileForm.setRegions(regions);
        List<String> positions = new ArrayList<>();
        positions.add("백엔드");
        profileForm.setPositions(positions);
        List<String> skills = new ArrayList<>();
        skills.add("스프링");
        profileForm.setSkills(skills);


        memberService.editProfile(profileForm, member.getNickname());


        //when
        ProfileForm findProfile = memberService.getProfile(member.getNickname());


        //then
//        assertThat(findProfile.getRegions().size()).isEqualTo(2);
    }
    @Test
    @DisplayName("모든멤버 조회")
    public void findMember() throws Exception {
        //given

        //when
        // 멤버를 우선 조회 한다 .
        PageRequest pageRequest = PageRequest.of(0, 12);
        Page<Member> result = memberDataRepository.searchPageMember(pageRequest);

        //then
        assertThat(result.getSize()).isEqualTo(12);

    }



}