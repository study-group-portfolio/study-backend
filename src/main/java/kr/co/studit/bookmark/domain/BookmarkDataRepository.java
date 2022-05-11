package kr.co.studit.bookmark.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkDataRepository extends JpaRepository<Bookmark, Long>, BookmarkCustomRepository {
    @Override
    Optional<Bookmark> findById(Long id);

    @Override
    void deleteById(Long bookmarkId);

}
