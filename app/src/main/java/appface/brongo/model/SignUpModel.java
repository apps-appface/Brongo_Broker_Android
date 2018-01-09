package appface.brongo.model;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Rohit Kumar on 7/12/2017.
 */
public class SignUpModel {
    String mobileNo;
    String emailId;
    String firstName;
    String lastName;
    String city;
    String referredBy;

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    String typeOfCompany;
    ArrayList<String> realEstateType;
    ApiModel.MicroMarketModel microMarket1;
    ApiModel.MicroMarketModel microMarket2;
    ApiModel.MicroMarketModel microMarket3;
    AddressModel officeAddress;
    AddressModel residentialAddress;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTypeOfCompany() {
        return typeOfCompany;
    }

    public void setTypeOfCompany(String typeOfCompany) {
        this.typeOfCompany = typeOfCompany;
    }

    public ArrayList<String> getRealEstateType() {
        return realEstateType;
    }

    public void setRealEstateType(ArrayList<String> realEstateType) {
        this.realEstateType = realEstateType;
    }

    public ApiModel.MicroMarketModel getMicroMarket1() {
        return microMarket1;
    }

    public void setMicroMarket1(ApiModel.MicroMarketModel microMarket1) {
        this.microMarket1 = microMarket1;
    }

    public ApiModel.MicroMarketModel getMicroMarket2() {
        return microMarket2;
    }

    public void setMicroMarket2(ApiModel.MicroMarketModel microMarket2) {
        this.microMarket2 = microMarket2;
    }

    public ApiModel.MicroMarketModel getMicroMarket3() {
        return microMarket3;
    }

    public void setMicroMarket3(ApiModel.MicroMarketModel microMarket3) {
        this.microMarket3 = microMarket3;
    }

    public AddressModel getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(AddressModel officeAddress) {
        this.officeAddress = officeAddress;
    }

    public AddressModel getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(AddressModel residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    String deviceId;
    String platform;

    public static class AddressModel{
        String line1;
        String line2;

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getLine2() {
            return line2;
        }

        public void setLine2(String line2) {
            this.line2 = line2;
        }

    }

    public static class LoginModel{
        String userId;
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        String deviceId;
        String platform;
        String appVersion;
        String osVersion;

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        String modelName;
    }
    public static class OtpVerificationModel{
        String mobileNo;
        int otp;
        String deviceId;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public int getOtp() {
            return otp;
        }

        public void setOtp(int otp) {
            this.otp = otp;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        String platform;
    }
}
