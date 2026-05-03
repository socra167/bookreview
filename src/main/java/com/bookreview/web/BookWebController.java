package com.bookreview.web;

import com.bookreview.domain.book.Book;
import com.bookreview.domain.book.BookService;
import com.bookreview.domain.review.ReviewService;
import com.bookreview.domain.review.ReviewSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
public class BookWebController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String query,
            Model model) {
        if (query != null && !query.isBlank()) {
            model.addAttribute("results", bookService.searchBooks(query));
            model.addAttribute("query", query);
        }
        return "index";
    }

    @GetMapping("/books/{isbn}")
    public String detail(
            @PathVariable String isbn,
            @RequestParam(defaultValue = "LATEST") ReviewSortType sort,
            @SessionAttribute(name = "loginMember", required = false) LoginMember loginMember,
            Model model) {
        Book book = bookService.getBookByIsbn(isbn);
        model.addAttribute("book", book);
        model.addAttribute("reviews", reviewService.getReviewsByBook(book.getId(), sort));
        model.addAttribute("sort", sort);
        model.addAttribute("loginMember", loginMember);
        return "book/detail";
    }
}
