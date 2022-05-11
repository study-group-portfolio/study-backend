package kr.co.studit.response;


import kr.co.studit.response.ResponseDto;
import kr.co.studit.response.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ErrorResponse {

    public static ResponseEntity<?> getErrorResponse(Exception e) {
        ResponseDto<String> errorResponse = new ResponseDto<String>();
        errorResponse.setStatus(Status.FALSE);
        errorResponse.setData(e.getMessage());
        return new ResponseEntity<ResponseDto>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<?> getErrorResponse(Exception e, HttpStatus status, String message) {
        ResponseDto<String> errorResponse = new ResponseDto<String>();
        errorResponse.setStatus(Status.FALSE);
        errorResponse.setData(e.getMessage());
        errorResponse.setMessage(message);
        return new ResponseEntity<ResponseDto>(errorResponse, status);
    }
}
