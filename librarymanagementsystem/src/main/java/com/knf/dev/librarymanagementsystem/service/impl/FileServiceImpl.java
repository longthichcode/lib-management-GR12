package com.knf.dev.librarymanagementsystem.service.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.knf.dev.librarymanagementsystem.constant.Item;
import com.knf.dev.librarymanagementsystem.service.AuthorService;
import com.knf.dev.librarymanagementsystem.service.BookService;
import com.knf.dev.librarymanagementsystem.service.CategoryService;
import com.knf.dev.librarymanagementsystem.service.FileService;
import com.knf.dev.librarymanagementsystem.service.PublisherService;
import com.knf.dev.librarymanagementsystem.util.Mapper;
import com.knf.dev.librarymanagementsystem.vo.AuthorRecord;
import com.knf.dev.librarymanagementsystem.vo.BookRecord;
import com.knf.dev.librarymanagementsystem.vo.CategoryRecord;
import com.knf.dev.librarymanagementsystem.vo.PublisherRecord;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Service
public class FileServiceImpl implements FileService {

    private final BookService bookService;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final CategoryService categoryService;

    public FileServiceImpl(BookService bookService, AuthorService authorService, PublisherService publisherService,
            CategoryService categoryService) {
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.publisherService = publisherService;
        this.bookService = bookService;
    }

    @Override
    public void exportCSV(String fileName, HttpServletResponse response)
            throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
        var item = Item.getItemByValue(fileName);
        // Đặt mã hóa UTF-8 và tiêu đề phản hồi
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + item.get().getFileName() + "\"");
        // Thêm BOM để hỗ trợ Excel
        response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        try (Writer writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8")) {
            switch (item.get()) {
                case BOOK:
                    StatefulBeanToCsv<BookRecord> csvWriter1 = getWriter(writer);
                    csvWriter1.write(Mapper.bookModelToVo(bookService.findAllBooks()));
                    break;
                case AUTHOR:
                    StatefulBeanToCsv<AuthorRecord> csvWriter2 = getWriter(writer);
                    csvWriter2.write(Mapper.authorModelToVo(authorService.findAllAuthors()));
                    break;
                case CATEGORY:
                    StatefulBeanToCsv<CategoryRecord> csvWriter3 = getWriter(writer);
                    csvWriter3.write(Mapper.categoryModelToVo(categoryService.findAllCategories()));
                    break;
                case PUBLISHER:
                    StatefulBeanToCsv<PublisherRecord> csvWriter4 = getWriter(writer);
                    csvWriter4.write(Mapper.publisherModelToVo(publisherService.findAllPublishers()));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported file type");
            }
        }
    }
    private static <T> StatefulBeanToCsv<T> getWriter(Writer writer) {
        return new StatefulBeanToCsvBuilder<T>(writer)
                .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER) 
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)      
                .withOrderedResults(false)
                .build();
    }
}
