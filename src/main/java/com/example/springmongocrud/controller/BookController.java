package com.example.springmongocrud.controller;

import java.util.List;
import java.util.Optional;

import com.example.springmongocrud.entity.Book;
import com.example.springmongocrud.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class BookController {

	@Autowired
	private BookRepository repository;

	@PostMapping("/saveBook")
	public ResponseEntity<String> saveBook(@RequestBody Book book) {
		repository.save(book);
		return ResponseEntity.ok("Added book with id : " + book.getId());
	}

	@Operation(summary = "Get all books")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found Books", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Book.class)))}),
			@ApiResponse(responseCode = "404", description = "No Books found", content = @Content) })
	@GetMapping("/getBooks")
	public ResponseEntity<List<Book>> getBooks() {
		List<Book> books = repository.findAll();
		if (books.isEmpty()) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.ok(books);
		}
	}

	@GetMapping("/getBook/{id}")
	public ResponseEntity<Book> getBook(@PathVariable int id) {
		if (id <= 0) {
			return ResponseEntity.badRequest().build();
		}
		Optional<Book> book = repository.findById(String.valueOf(id));
		if (book.isPresent()) {
			return ResponseEntity.ok().body(book.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable int id) {
		try {
			repository.deleteById(String.valueOf(id));
			return ResponseEntity.ok("Book with id " + id + " has been deleted");
		} catch (EmptyResultDataAccessException ex) {
			return ResponseEntity.notFound().build();
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while processing the request: " + ex.getMessage());
		}
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body("An error occurred while processing the request: " + ex.getMessage());
	}

}