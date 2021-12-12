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
public class SignupDto {

    @Email
    @NotBlank
    @ApiModelProperty(example = "example@studit.com")
    private String email;
    @NotBlank
    @ApiModelProperty(example = "studit1234")
    private String password;
    @NotBlank
    @ApiModelProperty(example = "studit1234")
    private String confirmPassword;
}
