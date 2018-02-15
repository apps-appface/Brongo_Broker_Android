package in.brongo.brongo_broker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rohit Kumar on 1/5/2018.
 */

public class PaymentHashResponseModel {
    @SerializedName("statusCode")
    private int statusCode;
    @SerializedName("data")
    private List<Data> data;
    @SerializedName("message")
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Data {
        @SerializedName("sha512")
        private String sha512;
        @SerializedName("txnid")
        private String txnid;
        @SerializedName("vapsHash")
        private String vapsHash;
        @SerializedName("paymentHash")
        private String paymentHash;
        @SerializedName("payment_related_details_for_mobile_sdk_hash")
        private String payment_related_details_for_mobile_sdk_hash;

        public String getSha512() {
            return sha512;
        }

        public void setSha512(String sha512) {
            this.sha512 = sha512;
        }

        public String getTxnid() {
            return txnid;
        }

        public void setTxnid(String txnid) {
            this.txnid = txnid;
        }

        public String getVapsHash() {
            return vapsHash;
        }

        public void setVapsHash(String vapsHash) {
            this.vapsHash = vapsHash;
        }

        public String getPaymentHash() {
            return paymentHash;
        }

        public void setPaymentHash(String paymentHash) {
            this.paymentHash = paymentHash;
        }

        public String getPayment_related_details_for_mobile_sdk_hash() {
            return payment_related_details_for_mobile_sdk_hash;
        }

        public void setPayment_related_details_for_mobile_sdk_hash(String payment_related_details_for_mobile_sdk_hash) {
            this.payment_related_details_for_mobile_sdk_hash = payment_related_details_for_mobile_sdk_hash;
        }
    }
}
