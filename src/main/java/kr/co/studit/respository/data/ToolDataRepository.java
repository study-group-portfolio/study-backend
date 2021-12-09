package kr.co.studit.respository.data;

import kr.co.studit.entity.Member;
import kr.co.studit.entity.Region;
import kr.co.studit.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolDataRepository extends JpaRepository<Tool, Long> {
    public Tool findToolByTool(String tool);
}
