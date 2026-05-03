package com.bookreview.domain.review;

import com.bookreview.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookOrderByCreatedAtDesc(Book book);

    List<Review> findByBookOrderByLikeCountDescCreatedAtDesc(Book book);
}
