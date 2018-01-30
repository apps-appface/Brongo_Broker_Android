package appface.brongo.model;

import java.util.ArrayList;

/**
 * Created by Rohit Kumar on 7/15/2017.
 */

public class ApiModel {
    public static class LogoutModel {
        String mobileNo;
        String deviceId;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        String platform;
        String userId;
    }

    public static class ProfileModel {
        int statusCode;
        String message;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<ProfileObject> getData() {
            return data;
        }

        public void setData(ArrayList<ProfileObject> data) {
            this.data = data;
        }

        ArrayList<ProfileObject> data;
    }

    public static class ProfileObject {
        String userId;
        String firstName;
        String lastName;
        String mobileNo;
        String emailId;
        String city;
        String profileImage;
        String micro1MarketName;
        String micro1MarketCity;
        String micro1Marketstate;

        public String getReferralId() {
            return referralId;
        }

        public void setReferralId(String referralId) {
            this.referralId = referralId;
        }

        public String getPlanExpiredTime() {
            return planExpiredTime;
        }

        public void setPlanExpiredTime(String planExpiredTime) {
            this.planExpiredTime = planExpiredTime;
        }

        int openDeals;
        int closedDeals;
        int totalDeals;
        String referralId;
        String planExpiredTime;
        int inventoryList;
        int reviewsCount;
        int referredNo;
        int credits;
        String planType;
        String addressProof;
        String iDProof;
        String reraCertificate;
        String typeOfCompany;

        public String getTypeOfCompany() {
            return typeOfCompany;
        }

        public void setTypeOfCompany(String typeOfCompany) {
            this.typeOfCompany = typeOfCompany;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getOpenDeals() {
            return openDeals;
        }

        public void setOpenDeals(int openDeals) {
            this.openDeals = openDeals;
        }

        public int getClosedDeals() {
            return closedDeals;
        }

        public void setClosedDeals(int closedDeals) {
            this.closedDeals = closedDeals;
        }

        public int getTotalDeals() {
            return totalDeals;
        }

        public void setTotalDeals(int totalDeals) {
            this.totalDeals = totalDeals;
        }

        public int getInventoryList() {
            return inventoryList;
        }

        public void setInventoryList(int inventoryList) {
            this.inventoryList = inventoryList;
        }

        public int getReviewsCount() {
            return reviewsCount;
        }

        public void setReviewsCount(int reviewsCount) {
            this.reviewsCount = reviewsCount;
        }

        public int getReferredNo() {
            return referredNo;
        }

        public void setReferredNo(int referredNo) {
            this.referredNo = referredNo;
        }

        public int getCredits() {
            return credits;
        }

        public void setCredits(int credits) {
            this.credits = credits;
        }

        public String getPlanType() {
            return planType;
        }

        public void setPlanType(String planType) {
            this.planType = planType;
        }

        public String getAddressProof() {
            return addressProof;
        }

        public void setAddressProof(String addressProof) {
            this.addressProof = addressProof;
        }

        public String getiDProof() {
            return iDProof;
        }

        public void setiDProof(String iDProof) {
            this.iDProof = iDProof;
        }

        public String getReraCertificate() {
            return reraCertificate;
        }

        public void setReraCertificate(String reraCertificate) {
            this.reraCertificate = reraCertificate;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getMicro1MarketName() {
            return micro1MarketName;
        }

        public ArrayList<String> getRealEstateType() {
            return realEstateType;
        }

        public void setRealEstateType(ArrayList<String> realEstateType) {
            this.realEstateType = realEstateType;
        }

        public Address getOfficeAddress() {
            return officeAddress;
        }

        public void setOfficeAddress(Address officeAddress) {
            this.officeAddress = officeAddress;
        }

        public Address getResidentialAddress() {
            return residentialAddress;
        }

        public void setResidentialAddress(Address residentialAddress) {
            this.residentialAddress = residentialAddress;
        }

        public void setMicro1MarketName(String micro1MarketName) {
            this.micro1MarketName = micro1MarketName;

        }

        public String getMicro1MarketCity() {
            return micro1MarketCity;
        }

        public void setMicro1MarketCity(String micro1MarketCity) {
            this.micro1MarketCity = micro1MarketCity;
        }

        public String getMicro1Marketstate() {
            return micro1Marketstate;
        }

        public void setMicro1Marketstate(String micro1Marketstate) {
            this.micro1Marketstate = micro1Marketstate;
        }

        public String getMicro2MarketName() {
            return micro2MarketName;
        }

        public void setMicro2MarketName(String micro2MarketName) {
            this.micro2MarketName = micro2MarketName;
        }

        public String getMicro2MarketCity() {
            return micro2MarketCity;
        }

        public void setMicro2MarketCity(String micro2MarketCity) {
            this.micro2MarketCity = micro2MarketCity;
        }

        public String getMicro2Marketstate() {
            return micro2Marketstate;
        }

        public void setMicro2Marketstate(String micro2Marketstate) {
            this.micro2Marketstate = micro2Marketstate;
        }

        public String getMicro3MarketName() {
            return micro3MarketName;
        }

        public void setMicro3MarketName(String micro3MarketName) {
            this.micro3MarketName = micro3MarketName;
        }

        public String getMicro3MarketCity() {
            return micro3MarketCity;
        }

        public void setMicro3MarketCity(String micro3MarketCity) {
            this.micro3MarketCity = micro3MarketCity;
        }

        public String getMicro3Marketstate() {
            return micro3Marketstate;
        }

        public void setMicro3Marketstate(String micro3Marketstate) {
            this.micro3Marketstate = micro3Marketstate;
        }

        String micro2MarketName;
        String micro2MarketCity;
        String micro2Marketstate;
        String micro3MarketName;
        String micro3MarketCity;
        String micro3Marketstate;
        String isActive;
        ArrayList<String> realEstateType;
        ArrayList<String> services;

        public ArrayList<String> getServices() {
            return services;
        }

        public void setServices(ArrayList<String> services) {
            this.services = services;
        }

        Address officeAddress;
        Address residentialAddress;

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }
    }

    public static class Address {
        String line1;
        String line2;
        String city;
        String state;
        String country;
        int pinCode;

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

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getPinCode() {
            return pinCode;
        }

        public void setPinCode(int pinCode) {
            this.pinCode = pinCode;
        }

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
    public static class ResponseModel{
        int statusCode;
        String message;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<String> getData() {
            return data;
        }

        public void setData(ArrayList<String> data) {
            this.data = data;
        }

        ArrayList<String> data;
    }

    public static class MicroMarketModel {
        String microMarketName;
        String microMarketCity;

        public String getMicroMarketName() {
            return microMarketName;
        }

        public void setMicroMarketName(String microMarketName) {
            this.microMarketName = microMarketName;
        }

        public String getMicroMarketCity() {
            return microMarketCity;
        }

        public void setMicroMarketCity(String microMarketCity) {
            this.microMarketCity = microMarketCity;
        }

        public String getMicroMarketState() {
            return microMarketState;
        }

        public void setMicroMarketState(String microMarketState) {
            this.microMarketState = microMarketState;
        }

        String microMarketState;
    }

    public static class UpdateStatusModel {
        String mobileNo;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        String userId;
        String status;
    }

    public static class ClientAcceptModel {
        String clientMobileNo;
        String brokerMobileNo;
        String postedUser;

        public String getPostedUser() {
            return postedUser;
        }

        public void setPostedUser(String postedUser) {
            this.postedUser = postedUser;
        }

        public String getClientMobileNo() {
            return clientMobileNo;
        }

        public void setClientMobileNo(String clientMobileNo) {
            this.clientMobileNo = clientMobileNo;
        }

        public String getBrokerMobileNo() {
            return brokerMobileNo;
        }

        public void setBrokerMobileNo(String brokerMobileNo) {
            this.brokerMobileNo = brokerMobileNo;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getPostingType() {
            return postingType;
        }

        public void setPostingType(String postingType) {
            this.postingType = postingType;
        }

        String propertyId;
        String postingType;
        String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class HomeProfileModel {
        int statusCode;
        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public ArrayList<HomeprofileObject> getData() {
            return data;
        }

        public void setData(ArrayList<HomeprofileObject> data) {
            this.data = data;
        }

        String message;
        ArrayList<HomeprofileObject> data;
    }

    public static class HomeprofileObject {
        String firstName;
        String brokerImage;
        String lastName;

        public String getImageBaseurl() {
            return imageBaseurl;
        }

        public void setImageBaseurl(String imageBaseurl) {
            this.imageBaseurl = imageBaseurl;
        }

        String planType;
        String referralId;
        String imageBaseurl;
        public String premiumPlan;

        public String getPremiumPlan() {
            return premiumPlan;
        }

        public void setPremiumPlan(String premiumPlan) {
            this.premiumPlan = premiumPlan;
        }
        public String getReferralId() {
            return referralId;
        }

        public void setReferralId(String referralId) {
            this.referralId = referralId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getBrokerImage() {
            return brokerImage;
        }

        public void setBrokerImage(String brokerImage) {
            this.brokerImage = brokerImage;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getPlanType() {
            return planType;
        }

        public void setPlanType(String planType) {
            this.planType = planType;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getMicroMarketName() {
            return microMarketName;
        }

        public void setMicroMarketName(String microMarketName) {
            this.microMarketName = microMarketName;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public int getNoOfClientsRated() {
            return noOfClientsRated;
        }

        public void setNoOfClientsRated(int noOfClientsRated) {
            this.noOfClientsRated = noOfClientsRated;
        }

        public int getCommission() {
            return commission;
        }

        public void setCommission(int commission) {
            this.commission = commission;
        }

        public int getNotificationsBadge() {
            return notificationsBadge;
        }

        public void setNotificationsBadge(int notificationsBadge) {
            this.notificationsBadge = notificationsBadge;
        }

        public int getPotemtialCommission() {
            return potemtialCommission;
        }

        public void setPotemtialCommission(int potemtialCommission) {
            this.potemtialCommission = potemtialCommission;
        }
        double cCommission;

        public double getcCommission() {
            return cCommission;
        }

        public void setcCommission(double cCommission) {
            this.cCommission = cCommission;
        }
        String isActive;
        String microMarketName;
        float rating;
        int noOfClientsRated;
        int commission;
        int notificationsBadge;
        int potemtialCommission;
    }

    public static class OpenDealModels {
        int statusCode;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<OpenDealObject> getData() {
            return data;
        }

        public void setData(ArrayList<OpenDealObject> data) {
            this.data = data;
        }

        String message;
        ArrayList<OpenDealObject> data;
    }

    public static class OpenDealObject {
        ArrayList<BuyAndRentModel> sellAndRentOut;
        ArrayList<BuyAndRentModel> buyAndRent;
        ArrayList<BuyAndRentModel> openDeals;
        ArrayList<BuyAndRentModel> closedDeals;


        public double getpCommission() {
            return pCommission;
        }

        public void setpCommission(double pCommission) {
            this.pCommission = pCommission;
        }

        public ArrayList<BuyAndRentModel> getSellAndRentOut() {
            return sellAndRentOut;
        }

        public ArrayList<BuyAndRentModel> getOpenDeals() {
            return openDeals;
        }

        public void setOpenDeals(ArrayList<BuyAndRentModel> openDeals) {
            this.openDeals = openDeals;
        }

        public ArrayList<BuyAndRentModel> getClosedDeals() {
            return closedDeals;
        }

        public void setClosedDeals(ArrayList<BuyAndRentModel> closedDeals) {
            this.closedDeals = closedDeals;
        }

        public void setSellAndRentOut(ArrayList<BuyAndRentModel> sellAndRentOut) {
            this.sellAndRentOut = sellAndRentOut;
        }

        public ArrayList<BuyAndRentModel> getBuyAndRent() {
            return buyAndRent;
        }

        public void setBuyAndRent(ArrayList<BuyAndRentModel> buyAndRent) {
            this.buyAndRent = buyAndRent;
        }


        double pCommission;
    }
    public static class BuyAndRentModel {
        String clientName;
        String clientMobileNo;
        String meetingLocation;
        String dateOfVisit;
        String timeOfVisit;
        String microMarketName;

        public boolean isClientRated() {
            return isClientRated;
        }

        public void setClientRated(boolean clientRated) {
            isClientRated = clientRated;
        }

        boolean isClientRated;
        public String getMicroMarketName() {
            return microMarketName;
        }

        public void setMicroMarketName(String microMarketName) {
            this.microMarketName = microMarketName;
        }

        public String getMeetingLocation() {
            return meetingLocation;
        }

        public void setMeetingLocation(String meetingLocation) {
            this.meetingLocation = meetingLocation;
        }

        public String getDateOfVisit() {
            return dateOfVisit;
        }

        public void setDateOfVisit(String dateOfVisit) {
            this.dateOfVisit = dateOfVisit;
        }

        public String getTimeOfVisit() {
            return timeOfVisit;
        }

        public void setTimeOfVisit(String timeOfVisit) {
            this.timeOfVisit = timeOfVisit;
        }

        public String getMeetAt() {
            return meetAt;
        }

        public void setMeetAt(String meetAt) {
            this.meetAt = meetAt;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public ArrayList<Double> getLatLong() {
            return latLong;
        }

        public void setLatLong(ArrayList<Double> latLong) {
            this.latLong = latLong;
        }

        String meetAt;
        String note;
        ArrayList<Double> latLong;
        float rating;
        String dealClosedTime;
        ArrayList<String> completedStatus;

        public String getClientMobileNo() {
            return clientMobileNo;
        }

        public void setClientMobileNo(String clientMobileNo) {
            this.clientMobileNo = clientMobileNo;
        }

        public String getDealClosedTime() {
            return dealClosedTime;
        }

        public void setDealClosedTime(String dealClosedTime) {
            this.dealClosedTime = dealClosedTime;
        }

        public ArrayList<ClientDetailsModel.SiteDetails> getSiteVisit() {
            return siteVisit;
        }

        public void setSiteVisit(ArrayList<ClientDetailsModel.SiteDetails> siteVisit) {
            this.siteVisit = siteVisit;
        }

        public ArrayList<String> getStatusUpdatedTimes() {
            return statusUpdatedTimes;
        }

        public void setStatusUpdatedTimes(ArrayList<String> statusUpdatedTimes) {
            this.statusUpdatedTimes = statusUpdatedTimes;
        }

        public ArrayList<String> getProperty() {
            return property;
        }

        public void setProperty(ArrayList<String> property) {
            this.property = property;
        }

        ArrayList<String> remainingStatus;
        ArrayList<ClientDetailsModel.SiteDetails>  siteVisit;
        ArrayList<String> statusUpdatedTimes;
        ArrayList<String> property;
        String postingType;
        String propertyId;
        String clientImage;

        public ArrayList<String> getCompletedStatus() {
            return completedStatus;
        }

        public void setCompletedStatus(ArrayList<String> completedStatus) {
            this.completedStatus = completedStatus;
        }

        String planType;

        public String getSubPropertyType() {
            return subPropertyType;
        }

        public void setSubPropertyType(String subPropertyType) {
            this.subPropertyType = subPropertyType;
        }

        String subPropertyType;

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public String getPostingType() {
            return postingType;
        }

        public void setPostingType(String postingType) {
            this.postingType = postingType;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getClientImage() {
            return clientImage;
        }

        public void setClientImage(String clientImage) {
            this.clientImage = clientImage;
        }

        public String getPlanType() {
            return planType;
        }

        public void setPlanType(String planType) {
            this.planType = planType;
        }


        public ArrayList<String> getRemainingStatus() {
            return remainingStatus;
        }

        public void setRemainingStatus(ArrayList<String> remainingStatus) {
            this.remainingStatus = remainingStatus;
        }

        public String getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(String propertyType) {
            this.propertyType = propertyType;
        }


        public int getMatchedProperty() {
            return matchedProperty;
        }

        public void setMatchedProperty(int matchedProperty) {
            this.matchedProperty = matchedProperty;
        }

        public Double getCommission() {
            return commission;
        }

        public void setCommission(Double commission) {
            this.commission = commission;
        }

        String propertyType;
        int matchedProperty;
        Double commission;
    }

    public static class InventoryModel {
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

        public ArrayList<InventoryPersoanlList> getData() {
            return data;
        }

        public void setData(ArrayList<InventoryPersoanlList> data) {
            this.data = data;
        }

        ArrayList<InventoryPersoanlList> data;
    }

    public static class InventoryPersoanlList {
        String client;
        String microMarketName;
        String microMarketCity;
        String microMarketState;
        String propertyType;
        String subPropertyType;
        String postingType;
        long budget;
        String propertyStatus;
        String clientName;
        String clientMobileNo;
        String emailId;
        String note;
        String propertyImage1;

        public String getClient() {
            return client;
        }

        public void setClient(String client) {
            this.client = client;
        }

        public String getMicroMarketName() {
            return microMarketName;
        }

        public void setMicroMarketName(String microMarketName) {
            this.microMarketName = microMarketName;
        }

        public String getMicroMarketCity() {
            return microMarketCity;
        }

        public void setMicroMarketCity(String microMarketCity) {
            this.microMarketCity = microMarketCity;
        }

        public String getMicroMarketState() {
            return microMarketState;
        }

        public void setMicroMarketState(String microMarketState) {
            this.microMarketState = microMarketState;
        }

        public String getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(String propertyType) {
            this.propertyType = propertyType;
        }

        public long getBudget() {
            return budget;
        }

        public void setBudget(long budget) {
            this.budget = budget;
        }

        public String getPropertyStatus() {
            return propertyStatus;
        }

        public void setPropertyStatus(String propertyStatus) {
            this.propertyStatus = propertyStatus;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getClientMobileNo() {
            return clientMobileNo;
        }

        public void setClientMobileNo(String clientMobileNo) {
            this.clientMobileNo = clientMobileNo;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getPropertyImage1() {
            return propertyImage1;
        }

        public void setPropertyImage1(String propertyImage1) {
            this.propertyImage1 = propertyImage1;
        }

        public String getPropertyImage2() {
            return propertyImage2;
        }

        public void setPropertyImage2(String propertyImage2) {
            this.propertyImage2 = propertyImage2;
        }

        public String getPropertyImage3() {
            return propertyImage3;
        }

        public void setPropertyImage3(String propertyImage3) {
            this.propertyImage3 = propertyImage3;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getBrokerMobileNo() {
            return brokerMobileNo;
        }

        public void setBrokerMobileNo(String brokerMobileNo) {
            this.brokerMobileNo = brokerMobileNo;
        }

        public String getSubPropertyType() {
            return subPropertyType;
        }

        public void setSubPropertyType(String subPropertyType) {
            this.subPropertyType = subPropertyType;
        }

        public String getPostingType() {
            return postingType;
        }

        public void setPostingType(String postingType) {
            this.postingType = postingType;
        }

        String propertyImage2;
        String propertyImage3;
        String propertyId;
        String brokerMobileNo;

        public String getBedRoomType() {
            return bedRoomType;
        }

        public void setBedRoomType(String bedRoomType) {
            this.bedRoomType = bedRoomType;
        }

        String bedRoomType;
    }

    public static class WalkThroughModel {
        String title;
        String subTitle;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }

        int imageId;
    }

    public static class RatingModel {
        String clientMobileNo;
        String brokerMobileNo;
        float rating;

        public String getClientMobileNo() {
            return clientMobileNo;
        }

        public void setClientMobileNo(String clientMobileNo) {
            this.clientMobileNo = clientMobileNo;
        }

        public String getBrokerMobileNo() {
            return brokerMobileNo;
        }

        public void setBrokerMobileNo(String brokerMobileNo) {
            this.brokerMobileNo = brokerMobileNo;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public ArrayList<String> getReview() {
            return review;
        }

        public void setReview(ArrayList<String> review) {
            this.review = review;
        }

        ArrayList<String> review;
    }

    public static class HistoricalModel {
        String clientName;
        String mobileNo;
        String clientImage;
        String planType;
        String siteVisit;
        String subPropertyType;
        String bedRoomType;
        String microMarketName;
        String furnishingStatus;
        String propertyType;
        String postingType;
        String propertyStatus;
        String timeOfSearching;
        String propertyId;
        String orientation;
        String budget;
        float rating;

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getClientImage() {
            return clientImage;
        }

        public void setClientImage(String clientImage) {
            this.clientImage = clientImage;
        }

        public String getPlanType() {
            return planType;
        }

        public void setPlanType(String planType) {
            this.planType = planType;
        }

        public String getSiteVisit() {
            return siteVisit;
        }

        public void setSiteVisit(String siteVisit) {
            this.siteVisit = siteVisit;
        }

        public String getSubPropertyType() {
            return subPropertyType;
        }

        public void setSubPropertyType(String subPropertyType) {
            this.subPropertyType = subPropertyType;
        }

        public String getBedRoomType() {
            return bedRoomType;
        }

        public void setBedRoomType(String bedRoomType) {
            this.bedRoomType = bedRoomType;
        }

        public String getMicroMarketName() {
            return microMarketName;
        }

        public void setMicroMarketName(String microMarketName) {
            this.microMarketName = microMarketName;
        }

        public String getFurnishingStatus() {
            return furnishingStatus;
        }

        public void setFurnishingStatus(String furnishingStatus) {
            this.furnishingStatus = furnishingStatus;
        }

        public String getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(String propertyType) {
            this.propertyType = propertyType;
        }

        public String getPostingType() {
            return postingType;
        }

        public void setPostingType(String postingType) {
            this.postingType = postingType;
        }

        public String getPropertyStatus() {
            return propertyStatus;
        }

        public void setPropertyStatus(String propertyStatus) {
            this.propertyStatus = propertyStatus;
        }

        public String getTimeOfSearching() {
            return timeOfSearching;
        }

        public void setTimeOfSearching(String timeOfSearching) {
            this.timeOfSearching = timeOfSearching;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getOrientation() {
            return orientation;
        }

        public void setOrientation(String orientation) {
            this.orientation = orientation;
        }

        public String getBudget() {
            return budget;
        }

        public void setBudget(String budget) {
            this.budget = budget;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public float getCommission() {
            return commission;
        }

        public void setCommission(float commission) {
            this.commission = commission;
        }

        public int getMatchedProperty() {
            return matchedProperty;
        }

        public void setMatchedProperty(int matchedProperty) {
            this.matchedProperty = matchedProperty;
        }

        public double getBudgetRange1() {
            return budgetRange1;
        }

        public void setBudgetRange1(double budgetRange1) {
            this.budgetRange1 = budgetRange1;
        }

        public double getBudgetRange2() {
            return budgetRange2;
        }

        public void setBudgetRange2(double budgetRange2) {
            this.budgetRange2 = budgetRange2;
        }

        public String getExpectedRent() {
            return expectedRent;
        }

        public void setExpectedRent(String expectedRent) {
            this.expectedRent = expectedRent;
        }

        public String getExpectedDeposit() {
            return expectedDeposit;
        }

        public void setExpectedDeposit(String expectedDeposit) {
            this.expectedDeposit = expectedDeposit;
        }

        public String getExpectedPrice() {
            return expectedPrice;
        }

        public void setExpectedPrice(String expectedPrice) {
            this.expectedPrice = expectedPrice;
        }

        float commission;
        int matchedProperty;
        double budgetRange1;
        double budgetRange2;
        String expectedRent;
        String expectedDeposit;
        String expectedPrice;
    }

    public static class NotificationModel {
        int statusCode;
        String id;
        String alertType;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAlertType() {
            return alertType;
        }

        public void setAlertType(String alertType) {
            this.alertType = alertType;
        }

        public boolean isRead() {
            return isRead;
        }

        public void setRead(boolean read) {
            isRead = read;
        }

        boolean isRead;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<NotificationChildModel> getData() {
            return data;
        }

        public void setData(ArrayList<NotificationChildModel> data) {
            this.data = data;
        }

        String message;
        ArrayList<NotificationChildModel> data;
    }

    public static class NotificationChildModel {
        String message;
        String clientName;
        String clientProfile;
        String userId;
        String builderName;
        String commission;

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        String propertyId;
        String projectName;
        String location;
        String mobileNo;
        String status;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getBuilderName() {
            return builderName;
        }

        public void setBuilderName(String builderName) {
            this.builderName = builderName;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getLocation() {
            return location;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public long getBudgetRange() {
            return budgetRange;
        }

        public void setBudgetRange(long budgetRange) {
            this.budgetRange = budgetRange;
        }

        public void setLocation(String location) {
            this.location = location;
        }
        public String getProjectType() {
            return projectType;
        }

        public void setProjectType(String projectType) {
            this.projectType = projectType;
        }

        public String getProjectStatus() {
            return projectStatus;
        }

        public void setProjectStatus(String projectStatus) {
            this.projectStatus = projectStatus;
        }

        public String getPlanType() {
            return planType;
        }

        public void setPlanType(String planType) {
            this.planType = planType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isRead() {
            return isRead;
        }

        public void setRead(boolean read) {
            isRead = read;
        }

        long budgetRange;
        String projectType;
        String projectStatus;
        String planType;
        String id;
        String alertType;
        boolean isRead;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getClientProfile() {
            return clientProfile;
        }

        public void setClientProfile(String clientProfile) {
            this.clientProfile = clientProfile;
        }

        public String getAlertType() {
            return alertType;
        }

        public void setAlertType(String alertType) {
            this.alertType = alertType;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        String days;
    }

    public static class ReferPlanModel {
        public int statusCode;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<ReferralObject> getData() {
            return data;
        }

        public void setData(ArrayList<ReferralObject> data) {
            this.data = data;
        }

        public String message;
        public ArrayList<ReferralObject> data;
    }

    public static class ReferralObject {
        public ArrayList<String> referralCout;

        public ArrayList<String> getReferralCout() {
            return referralCout;
        }

        public void setReferralCout(ArrayList<String> referralCout) {
            this.referralCout = referralCout;
        }

        public ArrayList<String> getReferralPrice() {
            return referralPrice;
        }

        public void setReferralPrice(ArrayList<String> referralPrice) {
            this.referralPrice = referralPrice;
        }

        public ArrayList<String> referralPrice;
    }

    public static class ReferralData {
        int statusCode;
        String message;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ReferralDataObject getData() {
            return data;
        }

        public void setData(ReferralDataObject data) {
            this.data = data;
        }

        ReferralDataObject data;
    }

    public static class ReferralDataObject {
        String mobileNo;
        String totalReferralBonus;
        String referralCount;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getTotalReferralBonus() {
            return totalReferralBonus;
        }

        public void setTotalReferralBonus(String totalReferralBonus) {
            this.totalReferralBonus = totalReferralBonus;
        }

        public String getReferralCount() {
            return referralCount;
        }

        public void setReferralCount(String referralCount) {
            this.referralCount = referralCount;
        }

        public ArrayList<referredBrokerObject> getReferredBroker() {
            return referredBroker;
        }

        public void setReferredBroker(ArrayList<referredBrokerObject> referredBroker) {
            this.referredBroker = referredBroker;
        }

        ArrayList<referredBrokerObject> referredBroker;
    }

    public static class referredBrokerObject {
        String name;
        String mobileNo;
        String image;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }
    }
    public static class SettingPlanModel{
        public int statusCode;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public SettingPlanObject getData() {
            return data;
        }

        public void setData(SettingPlanObject data) {
            this.data = data;
        }

        public String message;
        public SettingPlanObject data;
    }
    public static class SettingPlanObject{
        public String mobileNo;
        public boolean offers;
        public boolean builderProject;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public boolean isOffers() {
            return offers;
        }

        public void setOffers(boolean offers) {
            this.offers = offers;
        }

        public boolean isBuilderProject() {
            return builderProject;
        }

        public void setBuilderProject(boolean builderProject) {
            this.builderProject = builderProject;
        }

        public boolean isNotificationSound() {
            return notificationSound;
        }

        public void setNotificationSound(boolean notificationSound) {
            this.notificationSound = notificationSound;
        }

        public boolean notificationSound;
    }
    public static class SubscriptionObject{
        public String _id;
        public String name;
        public float amountToSub;
        public int duration;

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

        public float offers;
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

        public ArrayList<String> services;
        public ArrayList<String> conditions;
        public String actual;
        public float netForMonth;
        public float youPay;
    }

    public static class SubscriptionModel{
        public int statusCode;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<SubscriptionObject> getData() {
            return data;
        }

        public void setData(ArrayList<SubscriptionObject> data) {
            this.data = data;
        }

        public String message;
        public ArrayList<SubscriptionObject> data;
    }
    public static class MatchingModel{
        public String address;
        public String bhk;
        public String budget;
        public String propertystatus;
        public String name;
        public String inventory_type;
        public String image;
        public String commission;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getBhk() {
            return bhk;
        }

        public void setBhk(String bhk) {
            this.bhk = bhk;
        }

        public String getBudget() {
            return budget;
        }

        public void setBudget(String budget) {
            this.budget = budget;
        }

        public String getPropertystatus() {
            return propertystatus;
        }

        public void setPropertystatus(String propertystatus) {
            this.propertystatus = propertystatus;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getInventory_type() {
            return inventory_type;
        }

        public void setInventory_type(String inventory_type) {
            this.inventory_type = inventory_type;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCommission() {
            return commission;
        }

        public void setCommission(String commission) {
            this.commission = commission;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getPostingtype() {
            return postingtype;
        }

        public void setPostingtype(String postingtype) {
            this.postingtype = postingtype;
        }

        public String getPropertytype() {
            return propertytype;
        }

        public void setPropertytype(String propertytype) {
            this.propertytype = propertytype;
        }

        public String getSubproperty() {
            return subproperty;
        }

        public void setSubproperty(String subproperty) {
            this.subproperty = subproperty;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getDealid() {
            return dealid;
        }

        public void setDealid(String dealid) {
            this.dealid = dealid;
        }

        public String area;
        public String postingtype;
        public String propertytype;
        public String subproperty;
        public String mobile;
        public String dealid;
    }
    public static class ReviewModel {
        public int statusCode;
        public String message;

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public ArrayList<ReviewObject> getData() {
            return data;
        }

        public void setData(ArrayList<ReviewObject> data) {
            this.data = data;
        }

        public ArrayList<ReviewObject> data;
    }

        public static class ReviewObject{
            public int oneRating;
            public int twoRating;

            public int getOneRating() {
                return oneRating;
            }

            public void setOneRating(int oneRating) {
                this.oneRating = oneRating;
            }

            public int getTwoRating() {
                return twoRating;
            }

            public void setTwoRating(int twoRating) {
                this.twoRating = twoRating;
            }

            public int getThreeRating() {
                return threeRating;
            }

            public void setThreeRating(int threeRating) {
                this.threeRating = threeRating;
            }

            public int getFourRating() {
                return fourRating;
            }

            public void setFourRating(int fourRating) {
                this.fourRating = fourRating;
            }

            public int getFiveRating() {
                return fiveRating;
            }

            public void setFiveRating(int fiveRating) {
                this.fiveRating = fiveRating;
            }

            public ArrayList<ReviewChild> getReviews() {
                return reviews;
            }

            public void setReviews(ArrayList<ReviewChild> reviews) {
                this.reviews = reviews;
            }

            public int threeRating;
            public int fourRating;
            public int fiveRating;
            public ArrayList<ReviewChild> reviews;
        }
        public static class ReviewChild{
            public String name;
            public float rating;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public float getRating() {
                return rating;
            }

            public void setRating(float rating) {
                this.rating = rating;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public ArrayList<String> getReview() {
                return review;
            }

            public void setReview(ArrayList<String> review) {
                this.review = review;
            }

            public String duration;
            public ArrayList<String> review;
        }
        public static class ReviewApiModel{
            public String mobileNo;
        public int size;
        public int from;

            public String getMobileNo() {
                return mobileNo;
            }

            public void setMobileNo(String mobileNo) {
                this.mobileNo = mobileNo;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getFrom() {
                return from;
            }

            public void setFrom(int from) {
                this.from = from;
            }
        }
        public static class KnowlarityModel{
        String to;

            public String getTo() {
                return to;
            }

            public void setTo(String to) {
                this.to = to;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getPropertyId() {
                return propertyId;
            }

            public void setPropertyId(String propertyId) {
                this.propertyId = propertyId;
            }

            String from;
        String propertyId;
        }

        public static class BuilderAcceptModel{
        String userId;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getBrokerMobileNo() {
                return brokerMobileNo;
            }

            public void setBrokerMobileNo(String brokerMobileNo) {
                this.brokerMobileNo = brokerMobileNo;
            }

            public String getPropertyId() {
                return propertyId;
            }

            public void setPropertyId(String propertyId) {
                this.propertyId = propertyId;
            }

            String brokerMobileNo;
        String propertyId;
        String mobileNo;
        String emailId;

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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getBuilderId() {
                return builderId;
            }

            public void setBuilderId(String builderId) {
                this.builderId = builderId;
            }

            String name;
        String builderId;
        }
        public static class UnsubscribeModel{
        String mobileNo;
            String msg;

            public String getMobileNo() {
                return mobileNo;
            }

            public void setMobileNo(String mobileNo) {
                this.mobileNo = mobileNo;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public String getPlanType() {
                return planType;
            }

            public void setPlanType(String planType) {
                this.planType = planType;
            }

            String planType;
        }
    }

