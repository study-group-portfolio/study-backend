package kr.co.studit.repository.data;

import kr.co.studit.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface RegionDataRepository extends JpaRepository<Region, Long> {
    public Region findRegionByArea(String area);

}
