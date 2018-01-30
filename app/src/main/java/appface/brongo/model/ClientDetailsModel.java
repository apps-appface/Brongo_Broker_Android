package appface.brongo.model;

import java.util.ArrayList;

/**
 * Created by Rohit Kumar on 9/29/2017.
 */
public class ClientDetailsModel {
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

    public ArrayList<ClientDetailsObject> getData() {
        return data;
    }

    public void setData(ArrayList<ClientDetailsObject> data) {
        this.data = data;
    }

    ArrayList<ClientDetailsObject> data;

    public static class ClientDetailsObject{
        String clientMobileNo;
        String brokerMobileNo;
        String propertyId;
        double commission;
        String regDate;
        String reminder;

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

        public double getCommission() {
            return commission;
        }

        public void setCommission(double commission) {
            this.commission = commission;
        }

        public String getRegDate() {
            return regDate;
        }

        public void setRegDate(String regDate) {
            this.regDate = regDate;
        }

        public String getReminder() {
            return reminder;
        }

        public void setReminder(String reminder) {
            this.reminder = reminder;
        }

        public String getDealClosedTime() {
            return dealClosedTime;
        }

        public void setDealClosedTime(String dealClosedTime) {
            this.dealClosedTime = dealClosedTime;
        }

        public ArrayList<SiteDetails> getSiteVisit() {
            return siteVisit;
        }

        public void setSiteVisit(ArrayList<SiteDetails> siteVisit) {
            this.siteVisit = siteVisit;
        }

        public ArrayList<String> getCompletedstatus() {
            return Completedstatus;
        }

        public void setCompletedstatus(ArrayList<String> completedstatus) {
            Completedstatus = completedstatus;
        }

        public ArrayList<String> getRemainingstatus() {
            return remainingstatus;
        }

        public void setRemainingstatus(ArrayList<String> remainingstatus) {
            this.remainingstatus = remainingstatus;
        }

        String dealClosedTime;
        ArrayList<String> Completedstatus;
        ArrayList<String> remainingstatus;
        ArrayList<SiteDetails>  siteVisit;
        ArrayList<String> statusUpdatedTimes;

        public ArrayList<String> getStatusUpdatedTimes() {
            return statusUpdatedTimes;
        }

        public void setStatusUpdatedTimes(ArrayList<String> statusUpdatedTimes) {
            this.statusUpdatedTimes = statusUpdatedTimes;
        }
    }
    public static class SiteDetails{
        String propertyId;
        String propertyName;
        String siteVisitName;
        String dateOfVisit;
        String timeOfVisit;

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getSiteVisitName() {
            return siteVisitName;
        }

        public void setSiteVisitName(String siteVisitName) {
            this.siteVisitName = siteVisitName;
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

        String meetAt;
    }
    public static class LeadStatusModel{
        String clientMobileNo;
        String brokerMobileNo;
        String propertyId;
        String regDate;
        String postingType;

        public String getRemainder() {
            return remainder;
        }

        public void setRemainder(String remainder) {
            this.remainder = remainder;
        }

        public String getReminder() {
            return reminder;
        }

        public void setReminder(String reminder) {
            this.reminder = reminder;
        }

        String remainder;
        String reminder;

        public String getPostingType() {
            return postingType;
        }

        public void setPostingType(String postingType) {
            this.postingType = postingType;
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

        public String getRegDate() {
            return regDate;
        }

        public void setRegDate(String regDate) {
            this.regDate = regDate;
        }

        public String getAcceptedProperty() {
            return acceptedProperty;
        }

        public void setAcceptedProperty(String acceptedProperty) {
            this.acceptedProperty = acceptedProperty;
        }

        public String getAcceptedPropertyName() {
            return acceptedPropertyName;
        }

        public void setAcceptedPropertyName(String acceptedPropertyName) {
            this.acceptedPropertyName = acceptedPropertyName;
        }

        public String getSiteVisitName() {
            return siteVisitName;
        }

        public void setSiteVisitName(String siteVisitName) {
            this.siteVisitName = siteVisitName;
        }

        public String getMeetAt() {
            return meetAt;
        }

        public void setMeetAt(String meetAt) {
            this.meetAt = meetAt;
        }

        public String getDateOfVisit() {
            return dateOfVisit;
        }

        public void setDateOfVisit(String dateOfVisit) {
            this.dateOfVisit = dateOfVisit;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getVisitingPropertyId() {
            return visitingPropertyId;
        }

        public void setVisitingPropertyId(String visitingPropertyId) {
            this.visitingPropertyId = visitingPropertyId;
        }

        public String getTimeOfVisit() {
            return timeOfVisit;
        }

        public void setTimeOfVisit(String timeOfVisit) {
            this.timeOfVisit = timeOfVisit;
        }

        public double getCommission() {
            return commission;
        }

        public void setCommission(double commission) {
            this.commission = commission;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        String acceptedProperty;
        String acceptedPropertyName;
        String siteVisitName;
        String meetAt;
        String dateOfVisit;
        String propertyName;
        String visitingPropertyId;
        String timeOfVisit;
        double commission;
        int status;
    }
    public static class NotificatioModel{
        String clientMobileNo;
        String brokerMobileNo;

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
    }
    public static class ConnectedClientModel{
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

        public ArrayList<ConnectedClientObject> getData() {
            return data;
        }

        public void setData(ArrayList<ConnectedClientObject> data) {
            this.data = data;
        }

        String message;
        ArrayList<ConnectedClientObject> data;
    }
    public static class ConnectedClientObject{
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

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        String firstName;
    }
    public static class DealModel{
        String message;

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

        public ArrayList<DealContent> getData() {
            return data;
        }

        public void setData(ArrayList<DealContent> data) {
            this.data = data;
        }

        int statusCode;
        ArrayList<DealContent> data;
    }
    public static class DealContent{
        ArrayList<DealObject> content;
        ArrayList<String> images;
        String propertyId;

        public ArrayList<DealObject> getContent() {
            return content;
        }

        public void setContent(ArrayList<DealObject> content) {
            this.content = content;
        }

        public ArrayList<String> getImages() {
            return images;
        }

        public void setImages(ArrayList<String> images) {
            this.images = images;
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

        public String getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(String propertyType) {
            this.propertyType = propertyType;
        }

        String postingType;
        String propertyType;
    }
    public static class DealObject{
        String content;
        String title;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
    public static class PropertyModel{
        String brokerMobileNo;
        String clientMobileNo;
        String propertyId;
        String subPropertyType;

        public String getSubPropertyType() {
            return subPropertyType;
        }

        public void setSubPropertyType(String subPropertyType) {
            this.subPropertyType = subPropertyType;
        }

        public String getBrokerMobileNo() {
            return brokerMobileNo;
        }

        public void setBrokerMobileNo(String brokerMobileNo) {
            this.brokerMobileNo = brokerMobileNo;
        }

        public String getClientMobileNo() {
            return clientMobileNo;
        }

        public void setClientMobileNo(String clientMobileNo) {
            this.clientMobileNo = clientMobileNo;
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

        public String getPropertyType() {
            return propertyType;
        }

        public void setPropertyType(String propertyType) {
            this.propertyType = propertyType;
        }

        String postingType;
        String propertyType;
    }
    public static class MeetingModel{
        public String note;
        public String dateOfVisit;

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
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

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
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

        public ArrayList<Double> getLatLong() {
            return latLong;
        }

        public void setLatLong(ArrayList<Double> latLong) {
            this.latLong = latLong;
        }

        public String timeOfVisit;
        public String propertyId;
        public String clientMobileNo;
        public String brokerMobileNo;
        public ArrayList<Double> latLong;
    }
}
