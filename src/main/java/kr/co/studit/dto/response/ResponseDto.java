package kr.co.studit.dto.response;

import kr.co.studit.dto.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto<T> {
    private Status status;
    private T data;
    private String message;
}
