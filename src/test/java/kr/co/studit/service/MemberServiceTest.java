package kr.co.studit.service;

import kr.co.studit.dto.SigninDto;
import kr.co.studit.dto.SignupDto;
import kr.co.studit.entity.Member;
import kr.co.studit.repository.data.MemberDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberDataRepository memberDataRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("jinu@gmail.com");
        signupDto.setPassword("1234567");
        memberService.createMember(signupDto);
    }


    @DisplayName("회원 가입 테스트")
    @Test
    public void signup() throws Exception {
        //given
        SignupDto signupDto = new SignupDto();
        signupDto.setEmail("jinu94@gmail.com");
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
        signinDto.setEmail("jinu@gmail.com");
        signinDto.setPassword("1234567");
        //when


        Member findMember = memberService.signin(signinDto);
        //then
        assertThat(passwordEncoder.matches(signinDto.getPassword(), findMember.getPassword())).isTrue();
    }


}