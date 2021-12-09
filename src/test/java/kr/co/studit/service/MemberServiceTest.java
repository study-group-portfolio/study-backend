package kr.co.studit.service;

import kr.co.studit.dto.MemberDto;
import kr.co.studit.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {

    }


    @DisplayName("회원 가입 테스트")
    @Test
    public void signup() throws Exception {
        //given
        MemberDto memberDto = new MemberDto();
        memberDto.setEmail("jinu@gmail.com");
        memberDto.setEmail("1234567");
        //when
        memberService.createMember(memberDto);
        Member newMember = memberRepository.findByEmail(memberDto.getEmail());
        //then
        assertThat(newMember.getEmail()).isEqualTo(memberDto.getEmail());
        assertThat(newMember.getPwd()).isNotEqualTo(memberDto.getPwd());
    }


}