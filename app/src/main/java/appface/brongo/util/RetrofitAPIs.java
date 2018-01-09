package appface.brongo.util;


import appface.brongo.model.ApiModel;
import appface.brongo.model.BuilderModel;
import appface.brongo.model.ClientDetailsModel;
import appface.brongo.model.DeviceDetailsModel;
import appface.brongo.model.PaymentHashModel;
import appface.brongo.model.PaymentHashResponseModel;
import appface.brongo.model.SignInModel;
import appface.brongo.model.SignUpModel;
import appface.brongo.model.TokenModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 *
 */

public interface RetrofitAPIs {
    @POST("/QuickBroker/broker/signup")
    Call<ResponseBody>signUpApi(@Body SignUpModel signUpModel);

    @POST("/QuickBroker/broker/loginWithOTP")
    Call<SignInModel>signInApi(@Body SignUpModel.LoginModel loginModel);

    @POST("/QuickBroker/broker/OTPVerification")
    Call<ResponseBody>verify_otpApi(@Body SignUpModel.OtpVerificationModel otpVerificationModel);

    @POST("/QuickBroker/broker/deviceInfo")
    Call<ResponseBody>storeDevice(@Body DeviceDetailsModel deviceDetailsModel);

    @POST("/QuickBroker/broker/logout")
    Call<ResponseBody>logoutApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.LogoutModel logoutModel);

    @POST("/QuickBroker/broker/tokenGenerator")
    Call<TokenModel>generateToken(@Body DeviceDetailsModel.TokenGeneration tokenGeneration);

    @Multipart
    @POST("/QuickBroker/broker/uploadDocuments")
    Call<ResponseBody> uploadFile(@Part("mobileNo") RequestBody mobileNo,@Part("panCardNumber") RequestBody panCardNumber,@Part("reraRegistration") RequestBody reraRegistration,@Part MultipartBody.Part reraCertificate ,@Part MultipartBody.Part addressProof,@Part MultipartBody.Part IDProof);

    @GET("/QuickBroker/broker/getProfile")
    Call<ApiModel.ProfileModel> getProfile(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @POST("/QuickBroker/broker/changeStatus")
    Call<ResponseBody> updateStatusApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.UpdateStatusModel updateStatusModel);

    @POST("/QuickBroker/broker/acceptingClient")
    Call<ResponseBody> ClentAcceptApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ClientAcceptModel clientAcceptModel);

    @GET("/QuickBroker/broker/homePage")
    Call<ApiModel.HomeProfileModel> getHomeProfile(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @GET("/QuickBroker/broker/acceptedLeads")
    Call<ApiModel.OpenDealModels> getOpenClosedApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @POST("/QuickBroker/broker/dropLead")
    Call<ResponseBody> DropLeadApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ClientAcceptModel clientAcceptModel);

    @Multipart
    @POST("/QuickBroker/broker/addInventory")
    Call<ResponseBody> AddInventApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Part("propertyId") RequestBody propertyId,@Part("brokerMobileNo") RequestBody brokerMobileNo,@Part("client") RequestBody client,@Part("propertyType") RequestBody propertyType,@Part("bedRoomType") RequestBody bedRoomType,@Part("budget") RequestBody budget
            ,@Part("propertyStatus") RequestBody propertyStatus,@Part("clientName") RequestBody clientName,@Part("clientMobileNo") RequestBody clientMobileNo,@Part("emailId") RequestBody emailId,@Part("note") RequestBody note,@Part MultipartBody.Part propertyImage1 ,@Part MultipartBody.Part propertyImage2,@Part MultipartBody.Part propertyImage3,@Part("microMarketName") RequestBody microMarketName
            ,@Part("microMarketCity") RequestBody microMarketCity,@Part("microMarketState") RequestBody microMarketState,@Part("commission") RequestBody commission,@Part("subPropertyType") RequestBody subPropertyType);

    @POST("/QuickBroker/broker/deleteInventory")
    Call<ResponseBody> dropInventoryApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ClientAcceptModel clientAcceptModel);

    @GET("/QuickBroker/broker/fetchInventory")
    Call<ApiModel.InventoryModel> getInventoryApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @POST("/QuickBroker/broker/knowlarity")
    Call<ResponseBody> callKnowlarityApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.KnowlarityModel knowlarityModel);

    @POST("/QuickBroker/broker/fetchLeadStatus")
    Call<ClientDetailsModel> getClientDetailsApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ApiModel.ClientAcceptModel clientAcceptModel);

    @POST("/QuickBroker/broker/clientRating")
    Call<ResponseBody> updateRatingApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.RatingModel ratingModel);

    @POST("/QuickBroker/broker/updateLeadStatus1")
    Call<ResponseBody> updateLeadStatusApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ClientDetailsModel.LeadStatusModel leadStatusModel);

    @GET("/QuickBroker/broker/historicalDeal")
    Call<ApiModel.OpenDealModels> getHistoricalDeal(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @GET("/QuickBroker/broker/getNotifications")
    Call<ApiModel.NotificationModel> getNotificationApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo,@Query("from") int from,@Query("size") int size);

    @POST("/QuickBroker/broker/matchedInventory")
    Call<ApiModel.InventoryModel> getMatchingInventory(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.InventoryPersoanlList inventoryPersoanlList);

    @POST("/QuickBroker/broker/rejectLead")
    Call<ResponseBody> rejectLeadApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ClientAcceptModel clientAcceptModel);

    @GET("/QuickBroker/broker/referralBonus")
    Call<ApiModel.ReferPlanModel> getReferralPlanApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @GET("/QuickBroker/broker/referABroker")
    Call<ApiModel.ReferralData> getReferralApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @GET("/QuickBroker/broker/fetchSettings")
    Call<ApiModel.SettingPlanModel> getSettingsApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @POST("/QuickBroker/broker/settings")
    Call<ApiModel.SettingPlanModel> updateSettingApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.SettingPlanObject settingPlanObject);

    @GET("/QuickBroker/broker/getSubPlans")
    Call<ApiModel.SubscriptionModel> getSubscriptionPlanApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);
    @GET("/QuickBroker/broker/fetchSubPlan")
    Call<ApiModel.SubscriptionModel> getCurrentPlanApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo,@Query("subscriptionId") String subscriptionId);

    @POST("/QuickBroker/broker/getMyReviews")
    Call<ApiModel.ReviewModel> getReviewApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ReviewApiModel reviewApiModel);

    @GET("/QuickBroker/broker/readNotification")
    Call<ResponseBody> readNotificationApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo,@Query("id") String id);

    @POST("/QuickBroker/broker/acceptingBuilder")
    Call<ApiModel.ResponseModel> acceptBuilderApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ApiModel.BuilderAcceptModel builderAcceptModel);

    @POST("/QuickBroker/broker/registerClient")
    Call<ApiModel.ResponseModel> registertBuilderApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ApiModel.BuilderAcceptModel builderAcceptModel);

    @GET("/QuickBroker/broker/fetchBuilderInventory")
    Call<BuilderModel.BuilderDetailsModel> builderInventoryApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @POST("/QuickBroker/broker/payment")
    Call<PaymentHashResponseModel> getPaymentHash(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body PaymentHashModel paymentHashModel);

}
