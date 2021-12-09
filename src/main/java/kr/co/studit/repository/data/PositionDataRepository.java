package kr.co.studit.repository.data;

import kr.co.studit.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionDataRepository extends JpaRepository<Position, Long> {
    public Position findPositionByPositionName(String positionName);
}
