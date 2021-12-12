package kr.co.studit.dto;

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

    @ApiModelProperty(example = "example@studit.com")
    @Email
    @NotBlank
    private String email;
    @ApiModelProperty(example = "studit1234")
    private String password;

}
