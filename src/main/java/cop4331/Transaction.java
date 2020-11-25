package cop4331;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer tID;
    private Integer sID;
    private Boolean sAcctSavings;
    private Integer rID;
    private Boolean rAcctSavings;
    private Double amount;
    private String date;
    private String memo;
    
    public Integer getTID() {
        return tID;
    }

    public void setTID(Integer tID) {
        this.tID = tID;
    }
    
    public Integer getSID() {
        return sID;
    }

    public void setSID(Integer sID) {
        this.sID = sID;
    }

    public Boolean getsAcctSavings() {
        return sAcctSavings;
    }

    public void setsAcctSavings(Boolean sAcctSavings) {
        this.sAcctSavings = sAcctSavings;
    }

    public Integer getRID() {
        return rID;
    }

    public void setRID(Integer rID) {
        this.rID = rID;
    }

    public Boolean getrAcctSavings() {
        return rAcctSavings;
    }

    public void setrAcctSavings(Boolean rAcctSavings) {
        this.rAcctSavings = rAcctSavings;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}