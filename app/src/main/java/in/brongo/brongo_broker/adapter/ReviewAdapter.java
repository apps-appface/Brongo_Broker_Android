package in.brongo.brongo_broker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;


import java.util.ArrayList;

import in.brongo.brongo_broker.R;
import in.brongo.brongo_broker.model.ApiModel;
import in.brongo.brongo_broker.uiwidget.FlowLayout;

/**
 * Created by Rohit Kumar on 12/7/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_ITEM = 2;
    private int totalCount = 0;
    private int progress1,progress2,progress3,progress4,progress5;
    private FooterListener footerListener;
    private ArrayList<ApiModel.ReviewChild> reviewChildArrayList;
    private Context context;
    private boolean isVisible = false;
    private ArrayList<Integer> starCountList;

    public ReviewAdapter(Context context, ArrayList<ApiModel.ReviewChild> reviewChildArrayList,ArrayList<Integer> starCountList,FooterListener footerListener) {
        this.context = context;
        this.reviewChildArrayList = reviewChildArrayList;
        this.starCountList = starCountList;
        this.footerListener = footerListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_child, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            //Inflating header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_footer, parent, false);
            return new FooterViewHolder(itemView);
        } else return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        try {
            if (holder instanceof HeaderViewHolder) {
                if(starCountList.size()  > 0){
                    for(int i =0;i<starCountList.size();i++){
                        totalCount = totalCount + starCountList.get(i);
                    }
                    HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
                    headerHolder.count_one.setText(starCountList.get(0)+"");
                    headerHolder.count_two.setText(starCountList.get(1)+"");
                    headerHolder.count_three.setText(starCountList.get(2)+"");
                    headerHolder.count_four.setText(starCountList.get(3)+"");
                    headerHolder.count_five.setText(starCountList.get(4)+"");
                    if(totalCount != 0) {
                        progress1 = ((starCountList.get(0))* 100) / totalCount;
                        progress2 = ((starCountList.get(1))* 100) / totalCount;
                        progress3 = ((starCountList.get(2))* 100) / totalCount;
                        progress4 = ((starCountList.get(3))* 100) / totalCount;
                        progress5 = ((starCountList.get(4))* 100) / totalCount;
                    }
                        headerHolder.progress_one.setProgress(progress1);
                        headerHolder.progress_two.setProgress(progress2);
                        headerHolder.progress_three.setProgress(progress3);
                        headerHolder.progress_four.setProgress(progress4);
                        headerHolder.progress_five.setProgress(progress5);
                }

            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder footerHolder = (FooterViewHolder) holder;
                if(!isVisible){
                    footerHolder.footerText.setVisibility(View.GONE);
                }else{
                    footerHolder.footerText.setVisibility(View.VISIBLE);
                }
                footerHolder.footerText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(footerListener != null) {
                            footerListener.btnClick();
                        }
                    }
                });
            } else if (holder instanceof ItemViewHolder) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                ArrayList<String> reviewlist = reviewChildArrayList.get(position-1).getReview();
                if(reviewlist.size() > 1) {
                    addReview(reviewlist, itemViewHolder.flowLayout);
                }
                if(reviewlist.size()>0) {
                    itemViewHolder.review1.setText(reviewChildArrayList.get(position - 1).getReview().get(0));
                    itemViewHolder.review1.setVisibility(View.VISIBLE);
                }
                itemViewHolder.review_ratingbar.setRating(reviewChildArrayList.get(position-1).getRating());
                itemViewHolder.comment.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == reviewChildArrayList.size() + 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return reviewChildArrayList.size() + 2;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView count_one,count_two,count_three,count_four,count_five;
        ProgressBar progress_one,progress_two,progress_three,progress_four,progress_five;

        public HeaderViewHolder(View view) {
            super(view);
            count_one =  view.findViewById(R.id.one_star_count);
            progress_one = view.findViewById(R.id.review_progress1);
            count_two =  view.findViewById(R.id.two_star_count);
            progress_two = view.findViewById(R.id.review_progress2);
            count_three =  view.findViewById(R.id.three_star_count);
            progress_three = view.findViewById(R.id.review_progress3);
            count_four =  view.findViewById(R.id.four_star_count);
            progress_four = view.findViewById(R.id.review_progress4);
            count_five =  view.findViewById(R.id.five_star_count);
            progress_five = view.findViewById(R.id.review_progress5);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerText;

        public FooterViewHolder(View view) {
            super(view);
            footerText =  view.findViewById(R.id.footer_text);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView review1,comment,review_time;
        RatingBar review_ratingbar;
        FlowLayout flowLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            review1 =  itemView.findViewById(R.id.review_review1);
            comment =  itemView.findViewById(R.id.review_comment);
            review_time =  itemView.findViewById(R.id.review_child_time);
            flowLayout = itemView.findViewById(R.id.review_flowlayout);
            review_ratingbar = itemView.findViewById(R.id.review_child_ratingbar);
        }
    }
    public interface FooterListener{
        void btnClick();
    }
    public void setButton(boolean isVisible){
        this.isVisible = isVisible;
    }
    private void addReview(ArrayList<String> arrayList,FlowLayout flowlayout){
        for (int i = 1; i < arrayList.size(); i++) {
            try {
                final int position = i;
                View layout2 = LayoutInflater.from(context).inflate(R.layout.review_text, flowlayout, false);
                TextView review = layout2.findViewById(R.id.review_text_review);
                review.setText(arrayList.get(i));
                flowlayout.addView(layout2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
