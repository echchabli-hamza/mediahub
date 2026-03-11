package com.mediahub.mediaService.controller;

import com.mediahub.mediaService.dto.MediaInfoResponse;
import com.mediahub.mediaService.dto.MediaRequest;
import com.mediahub.mediaService.dto.MediaResponse;
import com.mediahub.mediaService.model.Category;
import com.mediahub.mediaService.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }


    @PostMapping
    public ResponseEntity<MediaResponse> create(@Valid @RequestBody MediaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mediaService.create(request));
    }

    @GetMapping
    public Page<MediaResponse> getAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return mediaService.getAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public MediaResponse getById(@PathVariable Long id) {
        return mediaService.getById(id);
    }

    @PutMapping("/{id}")
    public MediaResponse update(@PathVariable Long id, @Valid @RequestBody MediaRequest request) {
        return mediaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Public  endpoints 

    @GetMapping("/genre/{genre}")
    public List<MediaResponse> findByGenre(@PathVariable String genre) {
        return mediaService.findByGenre(genre);
    }

    @GetMapping("/category/{category}")
    public List<MediaResponse> findByCategory(@PathVariable Category category) {
        return mediaService.findByCategory(category);
    }

    @GetMapping("/search")
    public List<MediaResponse> searchByTitle(@RequestParam String title) {
        return mediaService.searchByTitle(title);
    }

    @GetMapping("/filter")
    public List<MediaResponse> filter(@RequestParam(required = false) String genre,
                                      @RequestParam(required = false) String category,
                                      @RequestParam(required = false) Integer year,
                                      @RequestParam(required = false) Double rating) {
        return mediaService.filter(genre, category, year, rating);
    }

    //  Inter-service endpoints

    @GetMapping("/{id}/exists")
    public Map<String, Boolean> exists(@PathVariable Long id) {
        return Map.of("exists", mediaService.exists(id));
    }

    @GetMapping("/{id}/info")
    public MediaInfoResponse getInfo(@PathVariable Long id) {
        return mediaService.getInfo(id);
    }
}
