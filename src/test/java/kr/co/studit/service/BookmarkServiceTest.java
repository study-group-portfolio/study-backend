package kr.co.studit.service;

import kr.co.studit.bookmark.dto.BookmarkRes;
import kr.co.studit.bookmark.service.BookmarkService;
import kr.co.studit.member.dto.SearchMemberDto;
import kr.co.studit.dto.search.CustomPage;
import kr.co.studit.bookmark.domain.Bookmark;
import kr.co.studit.member.domain.Member;
import kr.co.studit.bookmark.domain.BookmarkDataRepository;
import kr.co.studit.member.domain.MemberDataRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookmarkServiceTest {

    @Autowired
    MemberDataRepository memberDataRepository;
    @Autowired
    BookmarkDataRepository bookmarkDataRepository;
    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    EntityManager em;


    @Test
    @Order(0)
    @DisplayName("회원 북마크 생성")
    @Commit
    public void createMemberBookmark() throws Exception {
        //given
        Member user1 = memberDataRepository.findMemberByNickname("user1");
        Member user2 = memberDataRepository.findMemberByNickname("user2");
        Member user3 = memberDataRepository.findMemberByNickname("user3");
        Member user4 = memberDataRepository.findMemberByNickname("user4");
        Member user5 = memberDataRepository.findMemberByNickname("user5");


        //when
        BookmarkRes res = bookmarkService.createMemberBookmark(user1, user2);
        Bookmark memberBookmark3 = Bookmark.createMemberBookmark(user1, user3);
        Bookmark memberBookmark4 = Bookmark.createMemberBookmark(user1, user4);
        Bookmark memberBookmark5 = Bookmark.createMemberBookmark(user1, user5);
        bookmarkDataRepository.save(memberBookmark3);
        bookmarkDataRepository.save(memberBookmark4);
        bookmarkDataRepository.save(memberBookmark5);

        //then
        assertThat(res.getBookmarkId()).isNotZero();
    }
    @Test
    @Order(1)
    @DisplayName("회원의 회원북마크 리스트 조회")
    public void findMemberBookmarkList() throws Exception {
        //given

        Member user1 = memberDataRepository.findMemberByNickname("user1");

        //when
        bookmarkDataRepository.findAll();
        CustomPage<SearchMemberDto> memberBookmarkList = bookmarkService.findMemberBookmarkList(user1.getId(), PageRequest.of(0, 100));
        List<SearchMemberDto> content = memberBookmarkList.getContent();
        //then

    }

    @DisplayName("회원 북마크 제거")
    @Test
    @Order(2)
    public void deleteMemberBookmark() throws Exception {
        //given
        Member user1 = memberDataRepository.findMemberByNickname("user1");

        Member user2 = memberDataRepository.findMemberByNickname("user2");
        int beforeDelete = user1.getBookmarks().size();

        Long memberBookmarkId = user1.getBookmarks().get(0).getId();
        bookmarkService.deleteMemberBookmark(memberBookmarkId);
        //when


        int afterDelete = user1.getBookmarks().size();


        //then
        assertThat(beforeDelete).isNotEqualTo(afterDelete);

    }
}