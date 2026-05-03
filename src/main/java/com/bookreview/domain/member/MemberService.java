package com.bookreview.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 이메일로 회원을 조회하고, 없으면 새로 생성한다.
     * 이미 존재하는 이메일이면 닉네임 파라미터는 무시된다.
     */
    @Transactional
    public Member findOrCreate(String email, String nickname) {
        return memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        Member.builder().email(email).nickname(nickname).build()));
    }
}
