package rs.sbnz.service.user.dto;

public class LoginDTO {
    private String email;
    private String password;


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginDTO() {
    }


    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
