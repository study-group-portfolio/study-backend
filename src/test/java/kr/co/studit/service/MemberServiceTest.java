package kr.co.studit.service;

import kr.co.studit.config.AppProperties;
import kr.co.studit.dto.member.ProfileForm;
import kr.co.studit.dto.member.SearchMemberDto;
import kr.co.studit.dto.member.SigninDto;
import kr.co.studit.dto.member.SignupDto;
import kr.co.studit.dto.search.CustomPage;
import kr.co.studit.entity.Bookmark;
import kr.co.studit.entity.Region;
import kr.co.studit.entity.enums.OnOffStatus;
import kr.co.studit.entity.enums.StudyType;
import kr.co.studit.entity.member.Member;
import kr.co.studit.repository.RegionDataRepository;
import kr.co.studit.repository.bookmark.BookmarkDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import kr.co.studit.repository.member.MemberRegionDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    static final String EMAIL = "studit@studit.com";

    @Autowired
    MemberService memberService;
    @Autowired
    MemberDataRepository memberDataRepository;

    @Autowired
    BookmarkDataRepository bookmarkDataRepository;

    @Autowired
    MemberRegionDataRepository memberRegionDataRepository;

    @Autowired
    RegionDataRepository regionDataRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppProperties appProperties;

    @Autowired
    DataSource dataSource;

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
        List<String> positions = new ArrayList<>();
        positions.add("백엔드 개발자");
        List<String> skills = new ArrayList<>();
        skills.add("Java");
        skills.add("Spring");


    }




    @DisplayName("회원 가입 테스트")
    @Test
    public void signup() throws Exception {
        //given
        SignupDto signupDto = new SignupDto();
        signupDto.setNickname("스터디왕1");
        signupDto.setEmail("test"+EMAIL);
        signupDto.setPassword("1234567");
        //when
        memberService.createMember(signupDto);
        Member newMember = memberDataRepository.findMemberByEmail(signupDto.getEmail());
        System.out.println("appProperties = " + appProperties.getBackhost());
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
        memberService.editProfile(profileForm, member.getEmail());
        member.setPublicProfile(true);
        //then
        Member findMember = memberDataRepository.findMemberByEmail(EMAIL);
        assertThat(findMember.isPublicProfile()).isTrue();


    }

    @Test
    @DisplayName("프로필 활동 지역 수정")
    public void updateMemberRegion() throws Exception {
        //given
        Member member = memberDataRepository.findMemberByNickname("user0");


        ProfileForm profileForm = new ProfileForm();
        profileForm.setNickname("user00");
        profileForm.setBio("안녕하세요 백엔드 개발자 스터디왕 입니다");
        profileForm.setStudyType(StudyType.SHARE);
        profileForm.setOnOffStatus(OnOffStatus.ON);
        List<String> regions = new ArrayList<>();
        regions.add("대구");
        profileForm.setRegions(regions);
        List<String> positions = new ArrayList<>();
        positions.add("백엔드 개발자");
        profileForm.setPositions(positions);
        List<String> skills = new ArrayList<>();
        skills.add("Spring");
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
        assertThat(findMember.getRegions().get(0).getRegion().getArea().equals("대구")).isTrue();
        assertThat(findMember.getRegions().size()).isEqualTo(1);
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

        profileForm.setRegions(regions);
        List<String> positions = new ArrayList<>();
        positions.add("백엔드 개발자");
        profileForm.setPositions(positions);
        List<String> skills = new ArrayList<>();
        skills.add("Spring");
        profileForm.setSkills(skills);


        memberService.editProfile(profileForm, member.getEmail());


        //when
        ProfileForm findProfile = memberService.getProfile(member.getId());


        //then
//        assertThat(findProfile.getRegions().size()).isEqualTo(2);
    }
    @Test
    public void findArea() throws Exception {
        //given
        Member member = memberDataRepository.findMemberByNickname("user0");
//        String zone = member.getRegions().get(0).getRegion().getArea();
//        System.out.println("zone = " + zone);
//        assertThat(zone.equals("서울")).isTrue();
        memberDataRepository.delateRegions(member);
        member.getRegions().clear();
        String area = "서울";
        Region area1 = regionDataRepository.findRegionByArea(area);
        System.out.println("area1.getArea() = " + area1.getArea());
//        System.out.println(area1);

        //when

        //then

    }

    @Test
    public void createMemberRegion() throws Exception {
        //given
        Member member = memberDataRepository.findMemberByNickname("user0");
        List<String> area = new ArrayList<>();

        area.add("제주");
        area.add("서울");
        memberService.updateMemberRegion(area, member);
        member.getRegions().stream().forEach(memberRegion -> System.out.println("memberRegion.getRegion().getArea() = " + memberRegion.getRegion().getArea()));

        //when

        //then

    }
    @Test
    @DisplayName("모든멤버 조회")
    public void findMember() throws Exception {
        //given

        //when
        // 멤버를 우선 조회 한다 .
        PageRequest pageRequest = PageRequest.of(0, 12);
        Page<Member> result = memberDataRepository.searchPageMember( pageRequest);

        //then
        assertThat(result.getSize()).isEqualTo(12);

    }



    @Test
    public void searchBookmark() throws Exception {
        //given
        Member user1 = memberDataRepository.findMemberByNickname("user1");
        Member user2 = memberDataRepository.findMemberByNickname("user2");
        Member user3 = memberDataRepository.findMemberByNickname("user3");
        Bookmark bookmark1 = Bookmark.builder()
                .markMember(user1)
                .markedMember(user2)
                .build();

        Bookmark bookmark2 = Bookmark.builder()
                .markMember(user1)
                .markedMember(user3)
                .build();
        bookmarkDataRepository.save(bookmark1);
        bookmarkDataRepository.save(bookmark2);

        Pageable pageable = PageRequest.of(0, 40);

        //when

        CustomPage<SearchMemberDto> searchMemberDtos = memberService.searchMemberDto(user1, pageable);
        List<SearchMemberDto> content = searchMemberDtos.getContent();


        //then

    }



}