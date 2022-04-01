package kr.co.studit.dto.search;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class CustomPage<T> {
    private List<T> content;
    private CustomPageable pageable;

    public CustomPage(List<T> content, Page page) {
        this.content = content;
        this.pageable = new CustomPageable(page);
    }
}
