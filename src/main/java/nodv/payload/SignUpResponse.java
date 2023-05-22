package nodv.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nodv.model.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponse {
    private String id;
    private String username;
    private String email;
    private boolean gender;
    private Role role;
    private boolean isActive;
}