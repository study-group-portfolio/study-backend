package kr.co.studit.bookmark.infra.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.studit.bookmark.domain.Bookmark;
import kr.co.studit.bookmark.domain.BookmarkCustomRepository;
import kr.co.studit.entity.QBookmark;
import kr.co.studit.entity.study.QStudy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

import java.util.List;

import static kr.co.studit.entity.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class BookmarkCustomRepositoryImpl implements BookmarkCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public Page<Bookmark> findMemberBookmarkList(Long memberId, Pageable pageable) {
        QueryResults<Bookmark> results = queryFactory
                .selectFrom(bookmark)
                .where(bookmark.markMember.id.eq(memberId)
                        ,bookmark.markedStudy.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmark.createdDate.desc())
                .fetchResults();
        List<Bookmark> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Bookmark> findStudyBookmarkList(Long memberId, Pageable pageable) {
        QueryResults<Bookmark> results = queryFactory
                .selectFrom(bookmark)
                .where(bookmark.markMember.id.eq(memberId)
                        ,bookmark.markedMember.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(bookmark.createdDate.desc())
                .fetchResults();
        List<Bookmark> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }



    @Override
    public Bookmark findMarkeStudy(Long studyId, Long memberId) {
        Bookmark bookmark = queryFactory
                .selectFrom(QBookmark.bookmark)
                .leftJoin(QBookmark.bookmark.markedStudy, QStudy.study)
                .where(QBookmark.bookmark.markedStudy.id.eq(studyId).and(QBookmark.bookmark.markMember.id.eq(memberId)))
                .fetchOne();
        if (bookmark != null) {
            return bookmark;
        }
        return null;
    }
}
