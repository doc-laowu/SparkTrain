package RealTimeDial;

import java.io.Serializable;

/**
 * Bill Consumer Dashboard Bean
 * @author mengyao
 *
 */
public class BCD implements Serializable {
    private static final long serialVersionUID = 1749406742944513387L;
    private String billId;
    private double receivableAmount;
    private String saleTime;
    private String sdt;//yyyyMMdd
    private int hour;
    private String billType;
    private String shopId;
    private String shopEntityId;
    public BCD() {
        super();
    }
    public BCD(String billId, double receivableAmount, String saleTime, String billType, String shopId, String shopEntityId) {
        super();
        this.billId = billId;
        this.receivableAmount = receivableAmount;
        this.saleTime = saleTime;
        setDateTime(saleTime);
        this.billType = billType;
        this.shopId = shopId;
        this.shopEntityId = shopEntityId;
    }
    public String getBillId() {
        return billId;
    }
    public void setBillId(String billId) {
        this.billId = billId;
    }
    public double getReceivableAmount() {
        return receivableAmount;
    }
    public void setReceivableAmount(double receivableAmount) {
        this.receivableAmount = receivableAmount;
    }
    public String getSaleTime() {
        return saleTime;
    }
    public void setSaleTime(String saleTime) {
        this.saleTime = saleTime;
        setDateTime(saleTime);
    }
    public String getSdt() {
        return sdt;
    }
    public void setSdt(String sdt) {
        this.sdt = sdt;
    }
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public String getBillType() {
        return billType;
    }
    public void setBillType(String billType) {
        this.billType = billType;
    }
    public String getShopId() {
        return shopId;
    }
    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
    public String getShopEntityId() {
        return shopEntityId;
    }
    public void setShopEntityId(String shopEntityId) {
        this.shopEntityId = shopEntityId;
    }
    private void setDateTime(String saleTime) {
        if (null!=saleTime&&saleTime.length()==17) {
            this.sdt = saleTime.substring(0, 8);
            this.hour = Integer.parseInt(saleTime.substring(8, 10));
        }
    }
    @Override
    public String toString() {
        return  billId + "\t" + receivableAmount + "\t" + saleTime + "\t" + sdt + "\t" + billType + "\t" + shopId + "\t" + shopEntityId;
    }

}
