package Model;

import javafx.scene.layout.Priority;

import java.util.Date;

public class Admin {
    private  String  firstname;
    private  String  lasttname;
    private String Password;
    private String  roll;
    private Date logingDate;

    public Admin(String firstname, String lasttname, String password, String roll, Date logingDate) {
        this.firstname = firstname;
        this.lasttname = lasttname;
        Password = password;
        this.roll = roll;
        this.logingDate = logingDate;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLasttname() {
        return lasttname;
    }

    public String getPassword() {
        return Password;
    }
}
