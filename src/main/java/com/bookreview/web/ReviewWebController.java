package com.bookreview.web;

import com.bookreview.domain.book.BookService;
import com.bookreview.domain.like.ReviewLikeService;
import com.bookreview.domain.review.ReviewService;
import com.bookreview.domain.review.dto.ReviewCreateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ReviewWebController {

    private final BookService bookService;
    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    @PostMapping("/books/{isbn}/reviews")
    public String createReview(
            @PathVariable String isbn,
            @RequestParam String content,
            @RequestParam int rating,
            @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (loginMember == null) {
            return "redirect:/login?redirectUrl=" + request.getRequestURI();
        }
        long bookId = bookService.getBookByIsbn(isbn).getId();
        reviewService.createReview(bookId, loginMember.id(), new ReviewCreateRequest(content, rating));
        return "redirect:/books/" + isbn;
    }

    @PostMapping("/books/{isbn}/reviews/{reviewId}/likes")
    public String toggleLike(
            @PathVariable String isbn,
            @PathVariable Long reviewId,
            @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember,
            HttpServletRequest request) {
        if (loginMember == null) {
            return "redirect:/login?redirectUrl=/books/" + isbn;
        }
        reviewLikeService.toggleLike(reviewId, loginMember.id());
        return "redirect:/books/" + isbn;
    }
}
