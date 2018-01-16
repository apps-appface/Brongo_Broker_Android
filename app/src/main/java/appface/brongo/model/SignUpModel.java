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
   String microMarket1;
    String microMarket2;
    String microMarket3;
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

    public String getMicroMarket1() {
        return microMarket1;
    }

    public void setMicroMarket1(String microMarket1) {
        this.microMarket1 = microMarket1;
    }

    public String getMicroMarket2() {
        return microMarket2;
    }

    public void setMicroMarket2(String microMarket2) {
        this.microMarket2 = microMarket2;
    }

    public String getMicroMarket3() {
        return microMarket3;
    }

    public void setMicroMarket3(String microMarket3) {
        this.microMarket3 = microMarket3;
    }

    public ArrayList<String> getRealEstateType() {
        return realEstateType;
    }

    public void setRealEstateType(ArrayList<String> realEstateType) {
        this.realEstateType = realEstateType;
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
    public static class MarketModel{
        String message;
        int statusCode;

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

        public ArrayList<MarketObject> getData() {
            return data;
        }

        public void setData(ArrayList<MarketObject> data) {
            this.data = data;
        }

        ArrayList<MarketObject> data;
    }
    public static class MarketObject{
        String microMarketId;
        String name;
        String city;

        public String getMicroMarketId() {
            return microMarketId;
        }

        public void setMicroMarketId(String microMarketId) {
            this.microMarketId = microMarketId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public boolean isClientVisibility() {
            return clientVisibility;
        }

        public void setClientVisibility(boolean clientVisibility) {
            this.clientVisibility = clientVisibility;
        }

        public boolean isTrending() {
            return trending;
        }

        public void setTrending(boolean trending) {
            this.trending = trending;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        String state;
        boolean clientVisibility;
        boolean trending;
        boolean active;
    }
    public static class VenueModel{
        String message;
        int statusCode;

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

        public ArrayList<VenueObject> getData() {
            return data;
        }

        public void setData(ArrayList<VenueObject> data) {
            this.data = data;
        }

        ArrayList<VenueObject> data;

    }
    public static class VenueObject{
        String _id;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public ApiModel.Address getVenue() {
            return venue;
        }

        public void setVenue(ApiModel.Address venue) {
            this.venue = venue;
        }

        public ArrayList<SlotModel> getTimeSlots() {
            return timeSlots;
        }

        public void setTimeSlots(ArrayList<SlotModel> timeSlots) {
            this.timeSlots = timeSlots;
        }

        String date;
        ApiModel.Address venue;
        ArrayList<SlotModel> timeSlots;
    }
    public static class SlotModel{
        String slotName;
        String time;

        public String getSlotName() {
            return slotName;
        }

        public void setSlotName(String slotName) {
            this.slotName = slotName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContactPer() {
            return contactPer;
        }

        public void setContactPer(String contactPer) {
            this.contactPer = contactPer;
        }

        int brokersCount;

        public int getBrokersCount() {
            return brokersCount;
        }

        public void setBrokersCount(int brokersCount) {
            this.brokersCount = brokersCount;
        }

        String contactPer;

    }
    public static class BookedSlotModel{
        String mobileNo;
        String slotName;
        String date;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getSlotName() {
            return slotName;
        }

        public void setSlotName(String slotName) {
            this.slotName = slotName;
        }
    }
}
