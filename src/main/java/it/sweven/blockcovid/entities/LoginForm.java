package it.sweven.blockcovid.entities;

public class LoginForm {
    private String name;
    private String password;

    public LoginForm(String name, String password) {
	this.name = name;
	this.password = password;
    }

    public String getName() {
	return name;
    }
    
    public String getPassword() {
	return password;
    }
    
    @Override
    public boolean equals(Object o) {
	if(o instanceof LoginForm) {
	    LoginForm other = (LoginForm) o;
	    return name.equals(other.name) && password.equals(other.password);
	} else return false;
    }
}
