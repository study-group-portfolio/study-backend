package kr.co.studit.repository.bookmark;

import kr.co.studit.entity.Bookmark;
import kr.co.studit.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkCustomRepository {

    Page<Bookmark> findMemberBookmarkList(Long loginMemberId, Pageable pageable);

    Page<Bookmark> findStudyBookmarkList(Long loginMemberId, Pageable pageable);
    Bookmark findMarkeStudy(Long studyId ,Long memberId);
}
