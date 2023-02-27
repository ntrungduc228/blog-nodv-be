package nodv.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
public class AuthRequestMobile {
    @NotBlank
    String provider;

    @NotBlank
    String providerId;

    String email;
    String username;
    String avatar;
}