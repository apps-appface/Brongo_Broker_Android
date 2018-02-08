package appface.brongo.model;

/**
 * Created by Rohit Kumar on 7/14/2017.
 */

public class DeviceDetailsModel {
    String mobileNo;
    String userId;
    String appVersion;
    String platform;
    String osVersion;

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    String modelName;
    String deviceToken;
    String deviceId;

    public static class TokenGeneration{
        String mobileNo;
        String deviceId;
        String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

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

        String platform;
    }
}
