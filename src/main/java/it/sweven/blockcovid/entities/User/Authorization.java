package it.sweven.blockcovid.entities.User;

import org.springframework.security.core.GrantedAuthority;

enum Authorization implements GrantedAuthority {
    USER, ADMIN, CLEANER;

    @Override
    public String getAuthority() {
	return this.name();
    }
}
