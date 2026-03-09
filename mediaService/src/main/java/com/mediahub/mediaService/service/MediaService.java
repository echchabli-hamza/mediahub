package com.mediahub.mediaService.service;

import com.mediahub.mediaService.dto.MediaInfoResponse;
import com.mediahub.mediaService.dto.MediaRequest;
import com.mediahub.mediaService.dto.MediaResponse;
import com.mediahub.mediaService.model.Category;
import com.mediahub.mediaService.model.Media;
import com.mediahub.mediaService.repository.MediaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    // ---- CRUD ----

    public MediaResponse create(MediaRequest request) {
        Media media = new Media();
        applyRequest(media, request);
        return toResponse(mediaRepository.save(media));
    }

    public Page<MediaResponse> getAll(Pageable pageable) {
        return mediaRepository.findAll(pageable).map(this::toResponse);
    }

    public MediaResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public MediaResponse update(Long id, MediaRequest request) {
        Media media = findOrThrow(id);
        applyRequest(media, request);
        return toResponse(mediaRepository.save(media));
    }

    public void delete(Long id) {
        if (!mediaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found with id: " + id);
        }
        mediaRepository.deleteById(id);
    }

    // ---- Queries ----

    public List<MediaResponse> findByGenre(String genre) {
        return mediaRepository.findByGenreIgnoreCase(genre)
                .stream().map(this::toResponse).toList();
    }

    public List<MediaResponse> findByCategory(Category category) {
        return mediaRepository.findByCategory(category)
                .stream().map(this::toResponse).toList();
    }

    public List<MediaResponse> searchByTitle(String title) {
        return mediaRepository.findByTitleContainingIgnoreCase(title)
                .stream().map(this::toResponse).toList();
    }

    public List<MediaResponse> filter(String genre, String category, Integer releaseYear, Double rating) {
        Specification<Media> spec = (root, query, cb) -> cb.conjunction(); // Start with empty spec

        if (genre != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("genre")), genre.toLowerCase()));
        }
        if (category != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category"), Category.valueOf(category.toUpperCase())));
        }
        if (releaseYear != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("releaseYear"), releaseYear));
        }
        if (rating != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("rating"), rating));
        }

        return mediaRepository.findAll(spec).stream().map(this::toResponse).toList();
    }

    // ---- Inter-service endpoints ----

    public boolean exists(Long id) {
        return mediaRepository.existsById(id);
    }

    public MediaInfoResponse getInfo(Long id) {
        Media media = findOrThrow(id);
        return new MediaInfoResponse(media.getId(), media.getTitle(), media.getCategory(), media.getGenre());
    }

    // ---- Helpers ----

    private Media findOrThrow(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media not found with id: " + id));
    }

    private void applyRequest(Media media, MediaRequest request) {
        media.setTitle(request.title());
        media.setDescription(request.description());
        media.setGenre(request.genre());
        media.setCategory(request.category());
        media.setReleaseYear(request.releaseYear());
        media.setDuration(request.duration());
    }

    private MediaResponse toResponse(Media media) {
        return new MediaResponse(
                media.getId(),
                media.getTitle(),
                media.getDescription(),
                media.getGenre(),
                media.getCategory(),
                media.getReleaseYear(),
                media.getDuration(),
                media.getRating(),
                media.getCreatedAt(),
                media.getUpdatedAt()
        );
    }
}
