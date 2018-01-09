package appface.brongo.model;

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
        String projectName;
        String projectType;
        String landAreaUnits;
        String dimensionsOfBedRoom;
        String projectStatus;
        String availableStatus;
        String efficiencyRatio;
        String brochure;
        String pricingSheet;
        String floorPlan;
        String imageFiles;
        String elevationImage;

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

        public String getEfficiencyRatio() {
            return efficiencyRatio;
        }

        public void setEfficiencyRatio(String efficiencyRatio) {
            this.efficiencyRatio = efficiencyRatio;
        }

        public String getBrochure() {
            return brochure;
        }

        public void setBrochure(String brochure) {
            this.brochure = brochure;
        }

        public String getPricingSheet() {
            return pricingSheet;
        }

        public void setPricingSheet(String pricingSheet) {
            this.pricingSheet = pricingSheet;
        }

        public String getFloorPlan() {
            return floorPlan;
        }

        public void setFloorPlan(String floorPlan) {
            this.floorPlan = floorPlan;
        }

        public String getImageFiles() {
            return imageFiles;
        }

        public void setImageFiles(String imageFiles) {
            this.imageFiles = imageFiles;
        }

        public String getElevationImage() {
            return elevationImage;
        }

        public void setElevationImage(String elevationImage) {
            this.elevationImage = elevationImage;
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

        public int getNoOfTowers() {
            return noOfTowers;
        }

        public void setNoOfTowers(int noOfTowers) {
            this.noOfTowers = noOfTowers;
        }

        public int getNoOfFloors() {
            return noOfFloors;
        }

        public void setNoOfFloors(int noOfFloors) {
            this.noOfFloors = noOfFloors;
        }

        public int getLandArea() {
            return landArea;
        }

        public void setLandArea(int landArea) {
            this.landArea = landArea;
        }

        public int getNoOfBasements() {
            return noOfBasements;
        }

        public void setNoOfBasements(int noOfBasements) {
            this.noOfBasements = noOfBasements;
        }

        public int getNoOfBedRooms() {
            return noOfBedRooms;
        }

        public void setNoOfBedRooms(int noOfBedRooms) {
            this.noOfBedRooms = noOfBedRooms;
        }

        public int getPricePerSQFT() {
            return pricePerSQFT;
        }

        public void setPricePerSQFT(int pricePerSQFT) {
            this.pricePerSQFT = pricePerSQFT;
        }

        public int getNoOfCarPark() {
            return noOfCarPark;
        }

        public void setNoOfCarPark(int noOfCarPark) {
            this.noOfCarPark = noOfCarPark;
        }

        public int getTotalBrokers() {
            return totalBrokers;
        }

        public void setTotalBrokers(int totalBrokers) {
            this.totalBrokers = totalBrokers;
        }

        public ArrayList<String> getBankLoan() {
            return bankLoan;
        }

        public void setBankLoan(ArrayList<String> bankLoan) {
            this.bankLoan = bankLoan;
        }

        public ArrayList<String> getAmenities() {
            return amenities;
        }

        public void setAmenities(ArrayList<String> amenities) {
            this.amenities = amenities;
        }

        public ApiModel.MicroMarketModel getLocation() {
            return location;
        }

        public void setLocation(ApiModel.MicroMarketModel location) {
            this.location = location;
        }

        public ArrayList<BuilderBroker> getAcceptedBrokers() {
            return acceptedBrokers;
        }

        public void setAcceptedBrokers(ArrayList<BuilderBroker> acceptedBrokers) {
            this.acceptedBrokers = acceptedBrokers;
        }

        String status;
        String propertyId;
        int noOfTowers;
        int noOfFloors;
        int landArea;
        int noOfBasements;
        int noOfBedRooms;
        int pricePerSQFT;
        int noOfCarPark;
        int totalBrokers;
        ArrayList<String> bankLoan;
        ArrayList<String> amenities;
   ApiModel.MicroMarketModel location;
   ArrayList<BuilderBroker> acceptedBrokers;
    }

    public static class BuilderBroker{
        String mobileNo;

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getConnectedTime() {
            return connectedTime;
        }

        public void setConnectedTime(String connectedTime) {
            this.connectedTime = connectedTime;
        }

        String firstName;
        String connectedTime;
    }
}
