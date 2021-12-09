package kr.co.studit.respository.data;

import kr.co.studit.entity.Member;
import kr.co.studit.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface RegionDataRepository extends JpaRepository<Region, Long> {
    public Region findRegionByArea(String area);

}
