package in.brongo.brongo_broker.model;

import java.util.ArrayList;

/**
 * Created by Rohit Kumar on 12/29/2017.
 */

public class BuilderModel {
    public static class BuilderDetailsModel{
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

        public ArrayList<BuilderObject> getData() {
            return data;
        }

        public void setData(ArrayList<BuilderObject> data) {
            this.data = data;
        }

        ArrayList<BuilderObject> data;
    }
    public static class BuilderObject{
       String _id;
        String userId;
        String url;
        String channelPAgree;

        public String getChannelPAgree() {
            return channelPAgree;
        }

        public void setChannelPAgree(String channelPAgree) {
            this.channelPAgree = channelPAgree;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        String projectName;
        String projectType;
        String builderId;
        String landAreaUnits;
        String dimensionsOfBedRoom;
        String projectStatus;
        String availableStatus;
        ArrayList<String> bedRoomType;

        public ArrayList<String> getBedRoomType() {
            return bedRoomType;
        }

        public void setBedRoomType(ArrayList<String> bedRoomType) {
            this.bedRoomType = bedRoomType;
        }

        public String getBudget() {
            return budget;
        }

        public void setBudget(String budget) {
            this.budget = budget;
        }

        public int getLandArea() {
            return landArea;
        }

        public void setLandArea(int landArea) {
            this.landArea = landArea;
        }

        String budget;

        public String getBuilderId() {
            return builderId;
        }

        public void setBuilderId(String builderId) {
            this.builderId = builderId;
        }

        public String getMicroMarketId() {
            return microMarketId;
        }

        public void setMicroMarketId(String microMarketId) {
            this.microMarketId = microMarketId;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getSubLocation() {
            return subLocation;
        }

        public void setSubLocation(String subLocation) {
            this.subLocation = subLocation;
        }

        public ArrayList<String> getImageFiles() {
            return imageFiles;
        }

        public void setImageFiles(ArrayList<String> imageFiles) {
            this.imageFiles = imageFiles;
        }

        String microMarketId;
        String companyName;
        String subLocation;
        float commission;

        public float getCommission() {
            return commission;
        }

        public void setCommission(float commission) {
            this.commission = commission;
        }

        ArrayList<String> imageFiles;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getProjectType() {
            return projectType;
        }

        public void setProjectType(String projectType) {
            this.projectType = projectType;
        }

        public String getLandAreaUnits() {
            return landAreaUnits;
        }

        public void setLandAreaUnits(String landAreaUnits) {
            this.landAreaUnits = landAreaUnits;
        }

        public String getDimensionsOfBedRoom() {
            return dimensionsOfBedRoom;
        }

        public void setDimensionsOfBedRoom(String dimensionsOfBedRoom) {
            this.dimensionsOfBedRoom = dimensionsOfBedRoom;
        }

        public String getProjectStatus() {
            return projectStatus;
        }

        public void setProjectStatus(String projectStatus) {
            this.projectStatus = projectStatus;
        }

        public String getAvailableStatus() {
            return availableStatus;
        }

        public void setAvailableStatus(String availableStatus) {
            this.availableStatus = availableStatus;
        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }


        public int getNoOfBedRooms() {
            return noOfBedRooms;
        }

        public void setNoOfBedRooms(int noOfBedRooms) {
            this.noOfBedRooms = noOfBedRooms;
        }


        public void setPricePerSQFT(int pricePerSQFT) {
            this.pricePerSQFT = pricePerSQFT;
        }

        public int getTotalBrokers() {
            return totalBrokers;
        }

        public void setTotalBrokers(int totalBrokers) {
            this.totalBrokers = totalBrokers;
        }
        String status;
        String propertyId;
        public float getPricePerSQFT() {
            return pricePerSQFT;
        }

        public void setPricePerSQFT(float pricePerSQFT) {
            this.pricePerSQFT = pricePerSQFT;
        }

        int landArea;
        int noOfBedRooms;
        float pricePerSQFT;
        int totalBrokers;
    }

}
