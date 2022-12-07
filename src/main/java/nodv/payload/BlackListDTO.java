package nodv.payload;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlackListDTO {
    private String userId;
    private String postId;
}
