package appface.brongo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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

    public static class CurrentPlanModel{
        public String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public ArrayList<CurrentPlanObject> getData() {
            return data;
        }

        public void setData(ArrayList<CurrentPlanObject> data) {
            this.data = data;
        }

        public int statusCode;
        public ArrayList<CurrentPlanObject> data;
    }
    public static class CurrentPlanObject{
        public String _id;
        public String name;
        public String amountToSub;
        public String expireTime;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAmountToSub() {
            return amountToSub;
        }

        public void setAmountToSub(String amountToSub) {
            this.amountToSub = amountToSub;
        }

        public String getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(String expireTime) {
            this.expireTime = expireTime;
        }

        public int getExpireWithIn() {
            return expireWithIn;
        }

        public void setExpireWithIn(int expireWithIn) {
            this.expireWithIn = expireWithIn;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public float getOffers() {
            return offers;
        }

        public void setOffers(float offers) {
            this.offers = offers;
        }

        public ArrayList<String> getServices() {
            return services;
        }

        public void setServices(ArrayList<String> services) {
            this.services = services;
        }

        public ArrayList<String> getConditions() {
            return conditions;
        }

        public void setConditions(ArrayList<String> conditions) {
            this.conditions = conditions;
        }

        public ArrayList<SubPlanObject> getSubPlans() {
            return subPlans;
        }

        public void setSubPlans(ArrayList<SubPlanObject> subPlans) {
            this.subPlans = subPlans;
        }

        public int expireWithIn;
        public int duration;
        public float offers;
    public ArrayList<String> services;
    public ArrayList<String> conditions;
    public ArrayList<SubPlanObject> subPlans;
    }

    public static class SubPlanObject{
        public int duration;
        public float amountToSub;
        public float offers;
        public String name;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public float getAmountToSub() {
            return amountToSub;
        }

        public void setAmountToSub(float amountToSub) {
            this.amountToSub = amountToSub;
        }

        public float getOffers() {
            return offers;
        }

        public void setOffers(float offers) {
            this.offers = offers;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getPayed() {
            return payed;
        }

        public void setPayed(float payed) {
            this.payed = payed;
        }

        public float getAmountFMonth() {
            return amountFMonth;
        }

        public void setAmountFMonth(float amountFMonth) {
            this.amountFMonth = amountFMonth;
        }

        public float payed;
        public float amountFMonth;
    }
    }


