package kr.co.studit.entity;

import kr.co.studit.entity.member.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mark_member_id")
    private Member markMember;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member markedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    private Study markedStudy;

    @CreatedDate
    private LocalDateTime createdDate;

    public static Bookmark createMemberBookmark(Member markMember, Member markedMember) {
        Bookmark bookmark = new Bookmark();
        bookmark.markMember = markMember;
        bookmark.markedMember = markedMember;
        markMember.getBookmarks().add(bookmark);
        return bookmark;
    }

    public static Bookmark createStudyBookmark(Member markMember, Study markedStudy) {
        Bookmark bookmark = new Bookmark();
        bookmark.markMember = markMember;
        bookmark.markedStudy = markedStudy;
        markMember.getBookmarks().add(bookmark);
        return bookmark;
    }

    public void deleteMemberBookmark(Bookmark bookmark) {
        bookmark.getMarkMember().getBookmarks().remove(bookmark);
    }
}
