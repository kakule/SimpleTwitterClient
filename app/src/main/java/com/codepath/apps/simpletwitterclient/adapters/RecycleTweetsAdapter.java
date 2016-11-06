package com.codepath.apps.simpletwitterclient.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.View.LinkifiedTextView;
import com.codepath.apps.simpletwitterclient.models.Tweet;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created on 10/28/2016.
 */

public class RecycleTweetsAdapter extends
        RecyclerView.Adapter<RecycleTweetsAdapter.ViewHolder> {

    public static int DETAIL_VIEW = 0;
    public static int PROFILE_VIEW = 1;

    // Define listener member variable
    private OnItemClickListener listener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, int id);
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
        ImageView ivtweetPic;
        VideoView video_player_view;
        private Context context;

        public ViewHolder(Context context, final View itemView) {
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvrelativeTime = (TextView) itemView.findViewById(R.id.tvrelativeTime);
            tvBody = (LinkifiedTextView) itemView.findViewById(R.id.tvBody);
            ivtweetPic = (ImageView) itemView.findViewById(R.id.ivTweetPic);
            video_player_view = (VideoView) itemView.findViewById(R.id.video_view);
            this.context = context;

            // Setup the click listener for detail view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position, DETAIL_VIEW);
                        }
                    }
                }
            });

            //Setup the click listener for profile pic
            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position, PROFILE_VIEW);
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

        public ImageView getIvtweetPic() {
            return ivtweetPic;
        }

        public VideoView getVideo_player_view() {
            return video_player_view;
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
        ImageView ivtweetPic = viewHolder.getIvtweetPic();
        final VideoView vid_view = viewHolder.getVideo_player_view();

        //populate the data into the subviews
        tvUserName.setText(tweet.getUser().getName());
        tvUserName.setTypeface(null, Typeface.BOLD);
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvrelativeTime.setText(tweet.getRelativeTimeAgo());
        tvBody.setText(tweet.getBody());
        ivProfileImage.setImageResource(android.R.color.transparent);//clear out the old image
        ivtweetPic.setImageResource(android.R.color.transparent);//clear out the old image

        Glide.with(getContext()).load(tweet.getUser().getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(getContext(), 3, 3))
                .into(ivProfileImage);
        if (tweet.getMedia() != null && tweet.getMedia().getTweetPic() != null) {
            ivtweetPic.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(tweet.getMedia().getTweetPic())
                    .bitmapTransform(new RoundedCornersTransformation(getContext(), 10, 10))
                    .into(ivtweetPic);
        } else {
            ivtweetPic.setVisibility(View.GONE);
        }

        if (tweet.getMedia() != null && tweet.getMedia().getTweetvid() != null) {
            vid_view.setVisibility(View.VISIBLE);
            vid_view.setVideoPath(tweet.getMedia().getTweetvid());
            MediaController media_Controller = new MediaController(getContext());
            media_Controller.setAnchorView(vid_view);
            vid_view.setMediaController(media_Controller);
            vid_view.requestFocus();
            vid_view.start();
        } else {
            // Hide the controller
            vid_view.setVisibility(View.GONE);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTweets.size();
    }


}
