package kr.co.studit.service;

import kr.co.studit.dto.MemberDto;
import kr.co.studit.entity.Member;
import kr.co.studit.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member createMember(MemberDto memberDto) {
        Member newMember = Member.builder()
                .email(memberDto.getEmail())
                .pwd(passwordEncoder.encode(memberDto.getEmail())).build();
        memberRepository.save(newMember);

        return memberRepository.findByEmail(memberDto.getEmail());
    }
}
