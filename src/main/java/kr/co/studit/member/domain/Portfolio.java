package kr.co.studit.member.domain;

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
