package com.knf.dev.librarymanagementsystem.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.knf.dev.librarymanagementsystem.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.serialName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> search(@Param("keyword") String keyword, Pageable pageable);
}

