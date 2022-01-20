package kr.co.studit.repository;

import kr.co.studit.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionDataRepository extends JpaRepository<Region, Long> {
    Region findRegionByArea(String area);

}
