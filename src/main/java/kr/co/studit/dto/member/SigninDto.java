package kr.co.studit.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigninDto {

    @ApiModelProperty(example = "admin@studit.co.kr")
    @Email
    @NotBlank
    private String email;
    @ApiModelProperty(example = "12345678")
    private String password;

}
