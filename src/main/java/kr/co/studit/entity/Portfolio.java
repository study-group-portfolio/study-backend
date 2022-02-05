package kr.co.studit.entity;

import kr.co.studit.entity.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Portfolio {

    @Id @GeneratedValue
    @Column(name = "portfolio_id")
    private Long id;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "member_id")
    private Member member;

    public void addPortPolio(Portfolio portfolio, Member member) {
        member.getPortfolios().add(this);
    }

}
