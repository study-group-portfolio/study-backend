package kr.co.studit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MemberDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String pwd;
}
