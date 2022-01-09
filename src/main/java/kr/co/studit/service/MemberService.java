package kr.co.studit.service;

import kr.co.studit.dto.MemberDto;
import kr.co.studit.dto.ResponseDto;
import kr.co.studit.dto.SigninDto;
import kr.co.studit.dto.SignupDto;
import kr.co.studit.entity.member.Member;
import kr.co.studit.provider.TokenProvider;
import kr.co.studit.repository.data.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberDataRepository memberDataRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public Member createMember(SignupDto signupDto) {
        Member newMember = Member.builder()
                .email(signupDto.getEmail())
                .password(passwordEncoder.encode(signupDto.getPassword())).build();
        memberDataRepository.save(newMember);

        return memberDataRepository.findMemberByEmail(signupDto.getEmail());
    }

    public Member signin(SigninDto signinDto) {

        Member findMember = memberDataRepository.findMemberByEmail(signinDto.getEmail().strip());
        if (findMember != null && passwordEncoder.matches(signinDto.getPassword(), findMember.getPassword())) {
            return findMember;
        }
        return null;
    }


    public ResponseEntity<?> authenticate(SigninDto signinDto) {

        Member member = signin(signinDto);

        if (member != null) {
            String token = tokenProvider.create(member);
            MemberDto responseMemberDto = MemberDto.builder()
                    .email(member.getEmail())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseMemberDto);
        } else {
            ResponseDto responseDto = ResponseDto.builder()
                    .status("Login failed.")
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDto);
        }
    }
}
