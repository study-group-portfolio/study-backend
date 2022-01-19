package kr.co.studit.repository.bookmark;

import kr.co.studit.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

public interface BookmarkDataRepository extends JpaRepository<Bookmark, Long>, BookmarkCustomRepository {
    @Override
    Optional<Bookmark> findById(Long id);

    @Override
    void deleteById(Long bookmarkId);
}
