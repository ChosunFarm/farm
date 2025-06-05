package farm.farmshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberEditDTO {
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String username;

    private String phone;
    private String address;

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    private String newPassword;
    private String confirmPassword;
}
