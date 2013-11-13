package ca.ulaval.glo4003.models;

import play.data.validation.Constraints;

import javax.persistence.Column;
import java.io.Serializable;

public class User extends Record implements Serializable {

    @Column(unique = true)
    @Constraints.Email
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
