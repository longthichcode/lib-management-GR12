package com.knf.dev.librarymanagementsystem.controller;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.service.AuthorService;
import com.knf.dev.librarymanagementsystem.service.BookService;
import com.knf.dev.librarymanagementsystem.service.CategoryService;
import com.knf.dev.librarymanagementsystem.service.PublisherService;

@Controller
public class BookController {
	private final BookService bookService;
	private final AuthorService authorService;
	private final CategoryService categoryService;
	private final PublisherService publisherService;
	public BookController(PublisherService publisherService, CategoryService categoryService, BookService bookService,
			AuthorService authorService) {
		this.authorService = authorService;
		this.bookService = bookService;
		this.categoryService = categoryService;
		this.publisherService = publisherService;
	}

	@RequestMapping({ "/books", "/" })
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
		return "list-books";
	}

	
	@RequestMapping("/book/{id}")
	public String findBookById(@PathVariable("id") Long id, Model model) {
	    var book = bookService.findBookById(id);
	    if (book == null) {
	        throw new RuntimeException("Không tìm thấy sách với ID: " + id);
	    }
	    model.addAttribute("book", book);
	    return "view-book";  // Template HTML hiển thị thông tin sách
	}
	@GetMapping("/add")
	public String showCreateForm(Book book, Model model) {
		model.addAttribute("categories", categoryService.findAllCategories());
		model.addAttribute("authors", authorService.findAllAuthors());
		model.addAttribute("publishers", publisherService.findAllPublishers());
		return "add-book";
	}
	@RequestMapping("/add-book")
	public String createBook(Book book, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "add-book";
		}
		bookService.createBook(book);
		model.addAttribute("book", bookService.findAllBooks());
		return "redirect:/books";
	}

	@GetMapping("/update/{id}")
	public String showUpdateForm(@PathVariable("id") Long id, Model model) {
		model.addAttribute("categories", categoryService.findAllCategories());
		model.addAttribute("authors", authorService.findAllAuthors());
		model.addAttribute("publishers", publisherService.findAllPublishers());
		model.addAttribute("book", bookService.findBookById(id));
		return "update-book";
	}
	@RequestMapping("/update-book/{id}")
	public String updateBook(@PathVariable("id") Long id, Book book, BindingResult result, Model model) {
	    if (result.hasErrors()) {
	        book.setId(id);
	        return "update-book";
	    }
	    bookService.updateBook(book);
	    model.addAttribute("books", bookService.findAllBooks());
	    return "redirect:/books"; 
	}

	@RequestMapping("/remove-book/{id}")
	public String deleteBook(@PathVariable("id") Long id, Model model) {
		bookService.deleteBook(id);
		model.addAttribute("book", bookService.findAllBooks());
		return "redirect:/books";
	}
}