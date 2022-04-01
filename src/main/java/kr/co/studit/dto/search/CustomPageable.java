package kr.co.studit.dto.search;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class CustomPageable<T> {
   private int currentPage;
   private int size;
   private long totalElements;
   private int totalPages;
   private int numberOfElements;
   private boolean first;
   private boolean last;
   private boolean empty;

   public CustomPageable(Page<T> page) {
      this.currentPage = page.getNumber();
      this.size = page.getSize();
      this.totalElements = page.getTotalElements();
      this.totalPages = page.getTotalPages();
      this.numberOfElements = page.getNumberOfElements();
      this.first = page.isFirst();
      this.last = page.isLast();
      this.empty = page.isEmpty();

   }
}
