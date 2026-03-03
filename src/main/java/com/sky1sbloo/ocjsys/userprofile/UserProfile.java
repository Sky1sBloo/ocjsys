package com.sky1sbloo.ocjsys.userprofile;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name="id")
    private AuthUser authUser;

    private String name;
}
