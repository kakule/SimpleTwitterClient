package com.codepath.apps.simpletwitterclient;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created on 10/28/2016.
 */

public class RecycleTweetsAdapter extends
        RecyclerView.Adapter<RecycleTweetsAdapter.ViewHolder> {

    // Define listener member variable
    private OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvrelativeTime;
        LinkifiedTextView tvBody;
        private Context context;

        public ViewHolder(Context context, final View itemView) {
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvrelativeTime = (TextView) itemView.findViewById(R.id.tvrelativeTime);
            tvBody = (LinkifiedTextView) itemView.findViewById(R.id.tvBody);
            this.context = context;
            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }

        public ImageView getIvProfileImage() {
            return ivProfileImage;
        }

        public TextView getTvUserName() {
            return tvUserName;
        }

        public TextView getTvScreenName() {
            return tvScreenName;
        }

        public TextView getTvrelativeTime() {
            return tvrelativeTime;
        }

        public LinkifiedTextView getTvBody() {
            return tvBody;
        }

        public Context getContext() {
            return context;
        }

    }

    // Store a member variable for the contacts
    private List<Tweet> mTweets;
    // Store the context for easy access
    private Context mContext;

    public RecycleTweetsAdapter(Context context, List<Tweet> tweets) {
        this.mContext = context;
        this.mTweets = tweets;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public RecycleTweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        return new ViewHolder(context, tweetView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecycleTweetsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);
        // Set item views based on your views and data model
        ImageView ivProfileImage = viewHolder.getIvProfileImage();
        TextView tvUserName = viewHolder.getTvUserName();
        TextView tvScreenName = viewHolder.getTvScreenName();
        TextView tvrelativeTime = viewHolder.getTvrelativeTime();
        LinkifiedTextView tvBody =  viewHolder.getTvBody();

        //populate the data into the subviews
        tvUserName.setText(tweet.getUser().getName());
        tvUserName.setTypeface(null, Typeface.BOLD);
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvrelativeTime.setText(tweet.getRelativeTimeAgo());
        tvBody.setText(tweet.getBody());
        ivProfileImage.setImageResource(android.R.color.transparent);//clear out the old image
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(3, 3))
                .into(ivProfileImage);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }


}
