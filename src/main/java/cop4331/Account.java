package cop4331;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer aID;    // KEY
    private String uName;
    private String aName;
    private Boolean isSavings;
    private Double balance;

    public Integer getAID() {
        return aID;
    }

    public void setAID(Integer aID) {
        this.aID = aID;
    }

    public String getUName() {
        return uName;
    }

    public void setUName(String uName) {
        this.uName = uName;
    }

    public String getAName() {
        return aName;
    }

    public void setAName(String aName) {
        this.aName = aName;
    }

    public Boolean getIsSavings() {
        return isSavings;
    }

    public void setIsSavings(Boolean isSavings) {
        this.isSavings = isSavings;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
