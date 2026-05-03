package com.bookreview.domain.review;

import com.bookreview.domain.book.Book;
import com.bookreview.domain.book.BookRepository;
import com.bookreview.domain.member.Member;
import com.bookreview.domain.member.MemberRepository;
import com.bookreview.domain.review.dto.ReviewCreateRequest;
import com.bookreview.domain.review.dto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReviewResponse createReview(Long bookId, Long memberId, ReviewCreateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다. id=" + bookId));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + memberId));

        Review review = Review.builder()
                .book(book)
                .member(member)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        return ReviewResponse.from(reviewRepository.save(review));
    }

    public List<ReviewResponse> getReviewsByBook(Long bookId, ReviewSortType sortType) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("책을 찾을 수 없습니다. id=" + bookId));

        List<Review> reviews = switch (sortType) {
            case LATEST -> reviewRepository.findByBookOrderByCreatedAtDesc(book);
            case POPULAR -> reviewRepository.findByBookOrderByLikeCountDescCreatedAtDesc(book);
        };

        return reviews.stream().map(ReviewResponse::from).toList();
    }
}
