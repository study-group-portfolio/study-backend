package kr.co.studit.error;


import kr.co.studit.dto.response.ResponseDto;
import kr.co.studit.dto.enums.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ErrorResponse {

    public static ResponseEntity<?> getErrorResponse(Exception e) {
        ResponseDto<String> errorResponse = new ResponseDto<String>();
        errorResponse.setStatus(Status.FALSE);
        errorResponse.setData(e.getMessage());
        return new ResponseEntity<ResponseDto>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
