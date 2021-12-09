package kr.co.studit.respository.data;

import kr.co.studit.entity.Position;
import kr.co.studit.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionDataRepository extends JpaRepository<Position, Long> {
    public Position findPositionByPositionName(String positionName);
}
