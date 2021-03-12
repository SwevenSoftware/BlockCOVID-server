package it.sweven.blockcovid.entities.user;

import it.sweven.blockcovid.security.Authority;

import java.util.Set;

public class Credentials {
    private String username, password;
    private Set<Authority> authorities;

    public Credentials(String username, String password, Set<Authority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }
}
