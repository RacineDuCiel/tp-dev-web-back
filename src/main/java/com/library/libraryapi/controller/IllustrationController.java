package com.library.libraryapi.controller;

import com.library.libraryapi.dto.request.IllustrationRequest;
import com.library.libraryapi.dto.response.IllustrationResponse;
import com.library.libraryapi.service.IllustrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST pour les illustrations.
 * L'endpoint GET /api/illustrations/book/{bookId} permet de récupérer
 * toutes les illustrations d'un livre — utile pour la page détail d'un livre.
 */
@RestController
@RequestMapping("/api/illustrations")
@RequiredArgsConstructor
public class IllustrationController {

    private final IllustrationService illustrationService;

    @GetMapping
    public ResponseEntity<List<IllustrationResponse>> getAll() {
        return ResponseEntity.ok(illustrationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IllustrationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(illustrationService.getById(id));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<IllustrationResponse>> getByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(illustrationService.getByBookId(bookId));
    }

    @PostMapping
    public ResponseEntity<IllustrationResponse> create(@Valid @RequestBody IllustrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(illustrationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IllustrationResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody IllustrationRequest request) {
        return ResponseEntity.ok(illustrationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        illustrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
