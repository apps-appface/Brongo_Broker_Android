package appface.brongo.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rohit Kumar on 1/5/2018.
 */

public class PaymentHashModel {

    @SerializedName("amount")
    private String amount;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("productInfo")
    private String productInfo;
    @SerializedName("email")
    private String email;
    @SerializedName("mobileNo")
    private String mobileNo;
    @SerializedName("propertyId")
    private String propertyId;
    @SerializedName("paymentMode")
    private String paymentMode;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}

