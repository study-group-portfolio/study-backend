package kr.co.studit.dto;

import kr.co.studit.dto.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseListDto<T> {
    private Status status;
    private List<T> data;
}
