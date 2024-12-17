package com.knf.dev.librarymanagementsystem.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.service.AuthorService;
import com.knf.dev.librarymanagementsystem.service.BookService;
import com.knf.dev.librarymanagementsystem.service.CategoryService;
import com.knf.dev.librarymanagementsystem.service.PublisherService;

@Controller
public class UserController {

	private final BookService bookService;
	private final AuthorService authorService;
	private final CategoryService categoryService;
	private final PublisherService publisherService;

	public UserController(PublisherService publisherService, CategoryService categoryService, BookService bookService,
			AuthorService authorService) {
		this.authorService = authorService;
		this.bookService = bookService;
		this.categoryService = categoryService;
		this.publisherService = publisherService;
	}

	@RequestMapping({ "/reads", "/" })
	public String findAllBooks(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {

		var currentPage = page.orElse(1);
		var pageSize = size.orElse(7);

		var bookPage = bookService.findPaginated(PageRequest.of(currentPage - 1, pageSize));

		model.addAttribute("books", bookPage);

		var totalPages = bookPage.getTotalPages();
		if (totalPages > 0) {
			var pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		return "read-books";
	}
	
	@RequestMapping("/searchBook")
	public String searchBook(@RequestParam("title") String keyword,
	                         @RequestParam("page") Optional<Integer> page,
	                         @RequestParam("size") Optional<Integer> size,
	                         Model model) {

	    int currentPage = page.orElse(1);
	    int pageSize = size.orElse(5);

	    // Gọi repository để tìm kiếm có phân trang
	    Page<Book> bookPage = bookService.searchBooks(keyword, PageRequest.of(currentPage - 1, pageSize));

	    model.addAttribute("books", bookPage);
	    model.addAttribute("keyword", keyword);

	    // Tạo danh sách các số trang
	    int totalPages = bookPage.getTotalPages();
	    if (totalPages > 0) {
	        List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
	                .boxed()
	                .collect(Collectors.toList());
	        model.addAttribute("pageNumbers", pageNumbers);
	    }

	    return "read-books";
	}



	
	@RequestMapping("/choose-book/{id}")
	public String chooseBook(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
	    try {
	        bookService.chooseBook(id);  // Gọi service chọn sách
	        Book book = bookService.findBookById(id);
	        redirectAttributes.addFlashAttribute("message", "Bạn đã lấy sách " + book.getName());  // Thêm thông báo thành công
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Hết sách");  // Thêm thông báo lỗi
	    }

	    return "redirect:/reads";  // Chuyển hướng đến trang đọc
	}

//	@RequestMapping("/searchBook")
//	public String searchBook(@Param("keyword") String keyword, Model model) {
//
//		model.addAttribute("books", bookService.searchBooks(keyword));
//		model.addAttribute("keyword", keyword);
//		return "read-books";
//	}
}