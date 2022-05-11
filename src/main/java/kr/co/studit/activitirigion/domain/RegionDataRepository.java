package kr.co.studit.activitirigion.domain;

import kr.co.studit.activitirigion.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionDataRepository extends JpaRepository<Region, Long> {
    Region findRegionByArea(String area);

}
