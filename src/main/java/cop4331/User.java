package cop4331;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer uID;    // KEY
    private String uName;   // KEY
    private String fName;
    private String lName;
    private String pWord;

    public Integer getUID() {
        return uID;
    }

    public void setUID(Integer uID) {
        this.uID = uID;
    }

    public String getUName() {
        return uName;
    }

    public void setUName(String uName) {
        this.uName = uName;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getPWord() {
        return pWord;
    }

    public void setPWord(String pWord) {
        this.pWord = pWord;
    }
}
