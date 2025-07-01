package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "system_user")
public class SystemUser {
    @Id
    @SequenceGenerator(name = "system_user_gen", sequenceName = "system_user_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_user_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_role")
    private String userRole;

    public SystemUser(String userName, String userPassword, String userRole) {
        this.id = null;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRole = userRole;
    }

    public SystemUser(Long id, String userName, String userPassword, String userRole) {
        this.id = id;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRole = userRole;
    }

    @Override
    public SystemUser clone() {
        return new SystemUser(this.id, this.userName, this.userPassword, this.userRole);
    }

    @Override
    public String toString() {
        return "SystemUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }

}
