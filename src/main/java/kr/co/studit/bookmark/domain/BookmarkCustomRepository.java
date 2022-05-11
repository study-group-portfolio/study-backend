package kr.co.studit.bookmark.domain;

import kr.co.studit.bookmark.domain.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkCustomRepository {

    Page<Bookmark> findMemberBookmarkList(Long loginMemberId, Pageable pageable);

    Page<Bookmark> findStudyBookmarkList(Long loginMemberId, Pageable pageable);
    Bookmark findMarkeStudy(Long studyId ,Long memberId);
}
