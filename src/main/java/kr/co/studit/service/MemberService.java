package kr.co.studit.service;

import kr.co.studit.dto.MemberDto;
import kr.co.studit.entity.Member;
import kr.co.studit.repository.data.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberDataRepository memberDataRepository;
    private final PasswordEncoder passwordEncoder;

    public Member createMember(MemberDto memberDto) {
        Member newMember = Member.builder()
                .email(memberDto.getEmail())
                .pwd(passwordEncoder.encode(memberDto.getEmail())).build();
        memberDataRepository.save(newMember);

        return memberDataRepository.findMemberByEmail(memberDto.getEmail());
    }
}
