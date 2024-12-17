package com.knf.dev.librarymanagementsystem.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.knf.dev.librarymanagementsystem.entity.Book;

public interface BookService {

	public List<Book> findAllBooks();

	public Page<Book> searchBooks(String keyword, Pageable pageable);

	public Book findBookById(Long id);

	public void createBook(Book book);

	public void updateBook(Book book);

	public void deleteBook(Long id);
	
	public void chooseBook(Long id);

	public Page<Book> findPaginated(Pageable pageable);
}
