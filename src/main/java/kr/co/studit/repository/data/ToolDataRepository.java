package kr.co.studit.repository.data;

import kr.co.studit.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToolDataRepository extends JpaRepository<Tool, Long> {
    public Tool findToolByTool(String tool);
}
