package nodv.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    @Indexed(unique = true)
    private String email;
    @JsonIgnore
    private String password;
    private String avatar;
    private Boolean gender;
    private Integer followings;
    private Integer followers;
    private Role role;
    private Boolean isActive;
    private AuthProvider provider;
    private String providerId;

    private String bookmarkId;

    private Bookmark bookmark;

    private String bio;

}