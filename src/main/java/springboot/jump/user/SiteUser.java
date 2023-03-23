package springboot.jump.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    private String uuid;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteUser siteUser = (SiteUser) o;
        return Objects.equals(getId(), siteUser.getId()) && Objects.equals(getUsername(), siteUser.getUsername()) && Objects.equals(getPassword(), siteUser.getPassword()) && Objects.equals(getEmail(), siteUser.getEmail()) && getRole() == siteUser.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getPassword(), getEmail(), getRole());
    }
}
