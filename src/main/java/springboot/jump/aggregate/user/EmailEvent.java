package springboot.jump.aggregate.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailEvent {

    private String email;
    private String uuid;

    public EmailEvent(String email, String uuid) {
        this.email = email;
        this.uuid = uuid;
    }
}
