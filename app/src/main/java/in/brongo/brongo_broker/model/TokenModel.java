package in.brongo.brongo_broker.model;

import java.util.ArrayList;

/**
 * Created by Rohit Kumar on 7/6/2017.
 */

public class TokenModel {
    public String message;
    public int statusCode;
    public ArrayList<Data> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;

    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


    public class Data {
        private String mobileNo;
        private String accessToken;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
