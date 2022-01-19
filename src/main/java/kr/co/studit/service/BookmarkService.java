package kr.co.studit.service;

import kr.co.studit.dto.StudyDto;
import kr.co.studit.dto.bookmark.BookmarkRes;
import kr.co.studit.dto.member.SearchMemberDto;
import kr.co.studit.entity.Bookmark;
import kr.co.studit.entity.Study;
import kr.co.studit.entity.member.Member;
import kr.co.studit.repository.bookmark.BookmarkDataRepository;
import kr.co.studit.repository.data.StudyDataRepository;
import kr.co.studit.repository.member.MemberDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.el.ELException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkDataRepository bookmarkDataRepository;
    private final StudyService studyService;
    private final MemberService memberService;


    public BookmarkRes createMemberBookmark(Member markMember, Member markedMember) {
        Bookmark memberBookmark = Bookmark.createMemberBookmark(markMember, markedMember);
        Bookmark newMemberBookmark = bookmarkDataRepository.save(memberBookmark);
        BookmarkRes res = new BookmarkRes();
        res.setBookmarkId(newMemberBookmark.getId());
        res.setBookmarkState(true);
        return res;
    }

    public BookmarkRes deleteMemberBookmark(Long bookmarkId) {
        Bookmark bookmark = bookmarkDataRepository.findById(bookmarkId).orElseThrow(() -> new ELException("not found bookrmark")); // exception 처리 추후에 할 것
        bookmark.deleteMemberBookmark(bookmark);
        bookmarkDataRepository.delete(bookmark);
        BookmarkRes res = new BookmarkRes();
        res.setBookmarkState(false);
        return res;
    }

    public Page<SearchMemberDto> findMemberBookmarkList(Long loginMemberId, Pageable pageable) {
        Page<Bookmark> memberBookmarkList = bookmarkDataRepository.findMemberBookmarkList(loginMemberId, pageable);

        return createMemberBookmarkList(loginMemberId, memberBookmarkList);
    }

    public Page<StudyDto> findStudyBookmarkList(Long loginMemberId, Pageable pageable) {
        Page<Bookmark> studyBookmarkList = bookmarkDataRepository.findStudyBookmarkList(loginMemberId, pageable);

        return createStudyBookmarkList(loginMemberId, studyBookmarkList);
    }

    private Page<StudyDto> createStudyBookmarkList(Long loginMemberId, Page<Bookmark> bookmarks) {
        List<StudyDto> studyDtos = new ArrayList<>();
        for (Bookmark bookmark : bookmarks) {
            StudyDto studyDto = studyService.studyDtoMapper(bookmark.getMarkedStudy());
            studyDto.setBookmarkId(bookmark.getId());
            studyDto.setBookmarkState(true);
            studyDtos.add(studyDto);
        }
        Page<StudyDto> page = new PageImpl<>(studyDtos, bookmarks.getPageable(), bookmarks.getTotalElements());
        return page;
    }


    private Page<SearchMemberDto> createMemberBookmarkList(Long loginMemberId, Page<Bookmark> bookmarks) {
        List<SearchMemberDto> searchMemberDtos = new ArrayList<>();

        for (Bookmark bookmark: bookmarks) {
            SearchMemberDto searchMemberDto = new SearchMemberDto();
            searchMemberDto.setMemberId(bookmark.getMarkedMember().getId());
            searchMemberDto.setNickname(bookmark.getMarkedMember().getNickname());
            searchMemberDto.setPositionName(getStream(bookmark.getMarkedMember().getPositions()).map(memberPosition -> memberPosition.getPosition().getPositionName()).collect(toList()));
            searchMemberDto.setSkillName(getStream(bookmark.getMarkedMember().getSkills()).map(memberSkill -> memberSkill.getSkill().getSkillName()).collect(toList()));
            searchMemberDto.setArea(getStream(bookmark.getMarkedMember().getRegions()).map(memberRegion -> memberRegion.getRegion().getArea()).collect(toList()));
            // Dto당 북마크 아이디를 하나씩 넣어줘야함

            searchMemberDto.setBookmarkId(bookmark.getId());
            searchMemberDto.setBookmarkState(true);
            searchMemberDtos.add(searchMemberDto);

        }
        Page<SearchMemberDto> page = new PageImpl<>(searchMemberDtos, bookmarks.getPageable(), bookmarks.getTotalElements());
        return page;
    }

    public BookmarkRes createStudyBookmark(Member markMember, Study markedStudy) {


        Bookmark bookmark = Bookmark.createStudyBookmark(markMember, markedStudy);
        Bookmark newStudyBookmark = bookmarkDataRepository.save(bookmark);

        BookmarkRes res = new BookmarkRes();
        res.setBookmarkId(newStudyBookmark.getId());
        res.setBookmarkState(true);

        return res;
    }


    private <T> Stream<T> getStream(List<T> list) {
        return Optional.ofNullable(list).map(List::stream).orElseGet(Stream::empty);
    }


}
