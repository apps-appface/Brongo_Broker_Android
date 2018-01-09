package appface.brongo.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import appface.brongo.R;
import appface.brongo.adapter.ReviewAdapter;
import appface.brongo.model.ApiModel;
import appface.brongo.other.AllUtils;
import appface.brongo.other.NoInternetTryConnectListener;
import appface.brongo.util.AppConstants;
import appface.brongo.util.CircleTransform;
import appface.brongo.util.CustomApplicationClass;
import appface.brongo.util.RefreshTokenCall;
import appface.brongo.util.RetrofitAPIs;
import appface.brongo.util.RetrofitBuilders;
import appface.brongo.util.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReviewFragment extends Fragment implements ReviewAdapter.FooterListener,NoInternetTryConnectListener {
    private TextView review_broker_name,review_count,review_rating_value;
    private CircleImageView review_image;
    private ImageView review_broker_badge;
    private RatingBar review_ratingbar;
    private RecyclerView review_list;
    private ReviewAdapter reviewAdapter;
    private int from =0,totalCount = 0,size=10;
    ArrayList<ApiModel.ReviewChild> arrayList;
    ArrayList<Integer> ratingCountList;
    private SharedPreferences pref;
    private Context context;
    private boolean isemptyData = false;
    public ReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        initialise(view);
        return view;
    }
    private void initialise(View view){
        context = getActivity();
        arrayList = new ArrayList<>();
        ratingCountList = new ArrayList<>();
        pref = getActivity().getSharedPreferences(AppConstants.PREF_NAME,0);
        review_list = (RecyclerView)view.findViewById(R.id.review_recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        review_list.setLayoutManager(layoutManager);
        review_broker_name = (TextView)view.findViewById(R.id.review_broker_name);
        review_count = (TextView)view.findViewById(R.id.review_count1);
        review_rating_value = (TextView)view.findViewById(R.id.review_rating_value);
        review_image = (CircleImageView)view.findViewById(R.id.review_image);
        review_broker_badge = (ImageView)view.findViewById(R.id.review_broker_badge);
        review_ratingbar = (RatingBar)view.findViewById(R.id.review_ratingBar);
        reviewAdapter = new ReviewAdapter(context,arrayList,ratingCountList,this);
        review_list.setAdapter(reviewAdapter);
        setView();
        getReviewList(from,size);
    }
    private void setView(){
        review_broker_name.setText(pref.getString(AppConstants.FIRST_NAME,"")+" "+pref.getString(AppConstants.LAST_NAME,""));
        review_ratingbar.setRating(pref.getFloat(AppConstants.RATING,0.0f));
        String rate = String.format("%.1f",pref.getFloat(AppConstants.RATING,0.0f));
        review_rating_value.setText( rate+" out of 5 stars");
       /* Glide.with(context).load(pref.getString(AppConstants.USER_PIC,""))
                .diskCacheStrategy(DiskCacheStrategy.ALL).transform(new CircleTransform(context)).into(review_image);*/
        Glide.with(context)
                .load(pref.getString(AppConstants.USER_PIC,""))
                .apply(CustomApplicationClass.getRequestOption(true))
                .into(review_image);
    }
    private void getReviewList(final int from, final int size){
        if(Utils.isNetworkAvailable(context)) {
            Utils.LoaderUtils.showLoader(context);
            ApiModel.ReviewApiModel reviewApiModel = new ApiModel.ReviewApiModel();
            reviewApiModel.setFrom(from);
            reviewApiModel.setSize(size);
            reviewApiModel.setMobileNo(pref.getString(AppConstants.MOBILE_NUMBER, ""));
            RetrofitAPIs retrofitAPIs = RetrofitBuilders.getInstance().getAPIService(RetrofitBuilders.getBaseUrl());
            String deviceId = pref.getString(AppConstants.DEVICE_ID, "");
            String tokenaccess = pref.getString(AppConstants.TOKEN_ACCESS, "");
            Call<ApiModel.ReviewModel> call = retrofitAPIs.getReviewApi(tokenaccess, "android", deviceId, reviewApiModel);
            call.enqueue(new Callback<ApiModel.ReviewModel>() {
                @Override
                public void onResponse(Call<ApiModel.ReviewModel> call, Response<ApiModel.ReviewModel> response) {
                    Utils.LoaderUtils.dismissLoader();
                    if (response != null) {
                        String responseString = null;
                        if (response.isSuccessful()) {
                            ApiModel.ReviewModel reviewModel = response.body();
                            int statusCode = reviewModel.getStatusCode();
                            String message = reviewModel.getMessage();
                            if (statusCode == 200) {
                                ratingCountList.clear();
                                ratingCountList.add(reviewModel.getData().get(0).getOneRating());
                                ratingCountList.add(reviewModel.getData().get(0).getTwoRating());
                                ratingCountList.add(reviewModel.getData().get(0).getThreeRating());
                                ratingCountList.add(reviewModel.getData().get(0).getFourRating());
                                ratingCountList.add(reviewModel.getData().get(0).getFiveRating());
                                totalCount = 0;
                                for (int i = 0; i < 5; i++) {
                                    totalCount = totalCount + ratingCountList.get(i);
                                }
                                review_count.setText(totalCount + "");
                                ArrayList<ApiModel.ReviewChild> arrayList1 = reviewModel.getData().get(0).getReviews();
                                if (arrayList1.size() != 0) {
                                    arrayList.addAll(arrayList1);
                                }
                                if (arrayList1.size() < size) {
                                    isemptyData = true;
                                }
                            }
                            reviewAdapter.notifyDataSetChanged();
                        } else {
                            try {
                                responseString = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(responseString);
                                String message = jsonObject.optString("message");
                                int statusCode = jsonObject.optInt("statusCode");
                                if (statusCode == 417 && message.equalsIgnoreCase("Invalid Access Token")) {
                                    new AllUtils().getTokenRefresh(context);
                                    getReviewList(from, size);
                                } else {
                                    Utils.showToast(context, message);
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiModel.ReviewModel> call, Throwable t) {
                    Utils.LoaderUtils.dismissLoader();
                    Utils.showToast(context, "Some Problem Occured");
                }
            });
        }else{
            Utils.internetDialog(context,this);
        }
    }

    @Override
    public void btnClick() {
        if(!isemptyData) {
            from++;
            getReviewList(from, size);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Utils.LoaderUtils.dismissLoader();
    }

    @Override
    public void onTryReconnect() {
        getReviewList(from,size);
    }
}