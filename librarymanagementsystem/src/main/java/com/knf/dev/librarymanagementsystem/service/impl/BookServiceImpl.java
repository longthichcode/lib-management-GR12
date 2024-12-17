package com.knf.dev.librarymanagementsystem.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;

	public BookServiceImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public List<Book> findAllBooks() {
		return bookRepository.findAll();
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public Page<Book> searchBooks(String keyword, Pageable pageable) {
	    return bookRepository.search(keyword, pageable);
	}



	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public Book findBookById(Long id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format("Book not found with ID %d", id)));
	}

	@Override
	public void createBook(Book book) {
		bookRepository.save(book);
	}

	@Override
	public void updateBook(Book book) {
		bookRepository.save(book);
	}

	@Override
	public void deleteBook(Long id) {
		var book = bookRepository.findById(id)
				.orElseThrow(() -> new NotFoundException(String.format("Book not found with ID %d", id)));

		bookRepository.deleteById(book.getId());
	}

	@Override
	public Page<Book> findPaginated(Pageable pageable) {
		long startTime = System.currentTimeMillis(); // 开始计时

		List<Book> allBooks = findAllBooks();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<Book> list;

		if (allBooks.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, allBooks.size());
			list = allBooks.subList(startItem, toIndex);
		}

		var bookPage = new PageImpl<>(list, PageRequest.of(currentPage, pageSize), allBooks.size());

		long endTime = System.currentTimeMillis(); // 结束计时
		System.out.println("Optimized method execution time: " + (endTime - startTime) + "ms");

		return bookPage;
	}

	@Override
	public void chooseBook(Long id) {
	    // Tìm sách theo ID
	    var book = bookRepository.findById(id)
	            .orElseThrow(() -> new NotFoundException(String.format("Book not found with ID %d", id)));

	    // Kiểm tra nếu số lượng sách > 0, thì giảm số lượng đi 1
	    if (book.getQuantity() > 0) {
	        book.setQuantity(book.getQuantity() - 1);
	        bookRepository.save(book);  // Lưu lại sách sau khi thay đổi số lượng
	    } else {
	        throw new NotFoundException("Book is out of stock.");
	    }
	}




}
