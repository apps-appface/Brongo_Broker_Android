package in.brongo.brongo_broker.util;


import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.model.BuilderModel;
import in.brongo.brongo_broker.model.ClientDetailsModel;
import in.brongo.brongo_broker.model.DeviceDetailsModel;
import in.brongo.brongo_broker.model.PaymentHashModel;
import in.brongo.brongo_broker.model.PaymentHashResponseModel;
import in.brongo.brongo_broker.model.SignInModel;
import in.brongo.brongo_broker.model.SignUpModel;
import in.brongo.brongo_broker.model.TokenModel;
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
    @POST("broker/signup")
    Call<ResponseBody>signUpApi(@Body SignUpModel signUpModel);

    @POST("broker/loginWithOTP")
    Call<SignInModel>signInApi(@Body SignUpModel.LoginModel loginModel);

    @POST("broker/OTPVerification")
    Call<ResponseBody>verify_otpApi(@Body SignUpModel.OtpVerificationModel otpVerificationModel);

    @POST("broker/deviceInfo")
    Call<ResponseBody>storeDevice(@Body DeviceDetailsModel deviceDetailsModel);

    @POST("broker/logout")
    Call<ResponseBody>logoutApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.LogoutModel logoutModel);

    @POST("broker/tokenGenerator")
    Call<TokenModel>generateToken(@Body DeviceDetailsModel.TokenGeneration tokenGeneration);

    @Multipart
    @POST("broker/uploadDocuments")
    Call<ResponseBody> uploadFile(@Part("mobileNo") RequestBody mobileNo,@Part("panCardNumber") RequestBody panCardNumber,@Part("reraRegistration") RequestBody reraRegistration,@Part MultipartBody.Part reraCertificate ,@Part MultipartBody.Part addressProof,@Part MultipartBody.Part IDProof);

    @GET("broker/getProfile")
    Call<ApiModel.ProfileModel> getProfile(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @POST("broker/changeStatus")
    Call<ResponseBody> updateStatusApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.UpdateStatusModel updateStatusModel);

    @POST("broker/acceptingClient")
    Call<ResponseBody> ClentAcceptApi(@Body ApiModel.ClientAcceptModel clientAcceptModel);

    @GET("broker/homePage")
    Call<ApiModel.HomeProfileModel> getHomeProfile(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @GET("broker/acceptedLeads")
    Call<ApiModel.OpenDealModels> getOpenClosedApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @POST("broker/dropLead")
    Call<ResponseBody> DropLeadApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ClientAcceptModel clientAcceptModel);

    @Multipart
    @POST("broker/addInventory")
    Call<ResponseBody> AddInventApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Part("propertyId") RequestBody propertyId,@Part("brokerMobileNo") RequestBody brokerMobileNo,@Part("client") RequestBody client,@Part("propertyType") RequestBody propertyType,@Part("bedRoomType") RequestBody bedRoomType,@Part("budget") RequestBody budget
            ,@Part("propertyStatus") RequestBody propertyStatus,@Part("clientName") RequestBody clientName,@Part("clientMobileNo") RequestBody clientMobileNo,@Part("emailId") RequestBody emailId,@Part("note") RequestBody note,@Part MultipartBody.Part propertyImage1 ,@Part MultipartBody.Part propertyImage2,@Part MultipartBody.Part propertyImage3,@Part("microMarketName") RequestBody microMarketName
            ,@Part("microMarketCity") RequestBody microMarketCity,@Part("microMarketState") RequestBody microMarketState,@Part("commission") RequestBody commission,@Part("subPropertyType") RequestBody subPropertyType);

    @POST("broker/deleteInventory")
    Call<ResponseBody> dropInventoryApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ClientAcceptModel clientAcceptModel);

    @GET("broker/fetchInventory")
    Call<ApiModel.InventoryModel> getInventoryApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @POST("broker/knowlarity")
    Call<ResponseBody> callKnowlarityApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.KnowlarityModel knowlarityModel);

    @POST("broker/fetchLeadStatus")
    Call<ClientDetailsModel> getClientDetailsApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ApiModel.ClientAcceptModel clientAcceptModel);

    @POST("broker/clientRating")
    Call<ResponseBody> updateRatingApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.RatingModel ratingModel);

    @POST("broker/updateLeadStatus1")
    Call<ResponseBody> updateLeadStatusApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ClientDetailsModel.LeadStatusModel leadStatusModel);

    @GET("broker/historicalDeal")
    Call<ApiModel.OpenDealModels> getHistoricalDeal(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo);

    @GET("broker/getNotifications")
    Call<ApiModel.NotificationModel> getNotificationApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo,@Query("from") int from,@Query("size") int size);

    @POST("broker/matchedInventory")
    Call<ApiModel.MatchingResponseModel> getMatchingInventory(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.InventoryPersoanlList inventoryPersoanlList);

    @POST("broker/rejectLead")
    Call<ResponseBody> rejectLeadApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ClientAcceptModel clientAcceptModel);

    @GET("broker/referralBonus")
    Call<ApiModel.ReferPlanModel> getReferralPlanApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @GET("broker/referABroker")
    Call<ApiModel.ReferralData> getReferralApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @GET("broker/fetchSettings")
    Call<ApiModel.SettingPlanModel> getSettingsApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @POST("broker/settings")
    Call<ApiModel.SettingPlanModel> updateSettingApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.SettingPlanObject settingPlanObject);

    @GET("broker/getSubPlans")
    Call<ApiModel.SubscriptionModel> getSubscriptionPlanApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);
    @GET("broker/fetchSubPlan")
    Call<PaymentHashModel.CurrentPlanModel> getCurrentPlanApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo, @Query("subscriptionId") String subscriptionId);

    @POST("broker/getMyReviews")
    Call<ApiModel.ReviewModel> getReviewApi(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Body ApiModel.ReviewApiModel reviewApiModel);

    @GET("broker/readNotification")
    Call<ResponseBody> readNotificationApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo,@Query("id") String id);

    @POST("broker/acceptingBuilder")
    Call<ApiModel.ResponseModel> acceptBuilderApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ApiModel.BuilderAcceptModel builderAcceptModel);

    @POST("broker/registerClient")
    Call<ApiModel.ResponseModel> registertBuilderApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ApiModel.BuilderAcceptModel builderAcceptModel);

    @GET("broker/fetchBuilderInventory")
    Call<BuilderModel.BuilderDetailsModel> builderInventoryApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @POST("broker/payment")
    Call<PaymentHashResponseModel> getPaymentHash(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body PaymentHashModel paymentHashModel);

    @GET("broker/fetchMicroMarkets")
    Call<SignUpModel.MarketModel> fetchMarketApi(@Query("mobileNo") String mobileNo);

    @POST("broker/notiReceived")
    Call<ResponseBody>notificationApi(@Body ClientDetailsModel.NotificatioModel notificatioModel);

    @GET("broker/connectedClients")
    Call<ClientDetailsModel.ConnectedClientModel> connectedClientApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Query("mobileNo") String mobileNo);

    @POST("broker/getProperty")
    Call<ClientDetailsModel.DealModel>fetchDealApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId,@Body ClientDetailsModel.PropertyModel propertyModel);

    @POST("broker/builderInVentoryToMail")
    Call<ApiModel.ResponseModel> emailBuilderApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ApiModel.BuilderAcceptModel builderAcceptModel);

    @GET("broker/getAvailableSlots")
    Call<SignUpModel.VenueModel> fetchSlots(@Query("mobileNo") String mobileNo);

    @POST("broker/bookSlot")
    Call<ApiModel.ResponseModel>bookslotApi(@Body SignUpModel.BookedSlotModel bookedSlotModel);

    @POST("broker/scheduleMeeting")
    Call<ApiModel.ResponseModel> meetingApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ClientDetailsModel.MeetingModel meetingModel);

    @POST("broker/unSubscribe")
    Call<ApiModel.ResponseModel> unSubscribeApi(@Header("accessToken") String accessToken, @Header("platform") String platform, @Header("deviceId") String deviceId, @Body ApiModel.UnsubscribeModel unsubscribeModel);

    @GET("broker/activeLeads")
    Call<ApiModel.OpenDealModels> getActiveDeals(@Header("accessToken") String accessToken,@Header("platform") String platform,@Header("deviceId") String deviceId,@Query("mobileNo") String mobileNo,@Query("onlyConnected") boolean onlyConnected);
}
