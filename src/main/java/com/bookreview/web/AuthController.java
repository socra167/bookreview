package com.bookreview.web;

import com.bookreview.domain.member.Member;
import com.bookreview.domain.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(defaultValue = "/") String redirectUrl,
            Model model) {
        model.addAttribute("redirectUrl", redirectUrl);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String nickname,
            @RequestParam(defaultValue = "/") String redirectUrl,
            HttpSession session) {
        Member member = memberService.findOrCreate(email, nickname);
        session.setAttribute("loginMember", new LoginMember(member.getId(), member.getNickname()));
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
