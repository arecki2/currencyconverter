package currencyconverter;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rate {
    private String no;
    private Date effectiveDate;
    private double mid;

    public Rate(String no, Date effectiveDate, double mid) {
        this.no = no;
        this.effectiveDate = effectiveDate;
        this.mid = mid;
    }

    public String getNo() {
        return no;
    }

    public Date getDate() {
        return effectiveDate;
    }

    public double getMid() {
        return mid;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    @Override
    public String toString() {
        return "{\n" +
                "no: " + no + ";\n" +
                "effectiveDate: " + effectiveDate.toString() + ";\n" +
                "mid: " + mid + ";\n" +
                "}";
    }
}