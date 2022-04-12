package kr.co.studit.entity.study;

import kr.co.studit.entity.position.Position;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class StudyPosition {
    @Id
    @GeneratedValue
    @Column(name = "study_position_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    // 현재 지원자수
    private Integer count=0;

    // 총 지원수
    private Integer totalCount=0;
    //TODO 포지션 모집 상태, 스터디 모집 상태(모집기간이 끝난 경우, 모든 포지션의 모집상태가 true인 경우 모집이 끝남)

    // default = true 모집
    private boolean recruited;

    public void setStudy(Study study) {
        this.study = study;
        study.getStudyPosition().add(this);
    }

    public boolean isRecruited() {
        if (this.count < this.totalCount) {
            this.recruited = false;
        }else {
            this.recruited = true;
        }
        return this.recruited;
    }

}
