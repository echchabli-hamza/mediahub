package com.mediahub.mediaService.repository;

import com.mediahub.mediaService.model.Category;
import com.mediahub.mediaService.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long>, JpaSpecificationExecutor<Media> {

    List<Media> findByGenreIgnoreCase(String genre);

    List<Media> findByCategory(Category category);

    List<Media> findByTitleContainingIgnoreCase(String title);
}
