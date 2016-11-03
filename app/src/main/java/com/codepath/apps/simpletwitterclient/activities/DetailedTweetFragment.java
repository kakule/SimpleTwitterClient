package com.codepath.apps.simpletwitterclient.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.View.LinkifiedTextView;
import com.codepath.apps.simpletwitterclient.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created on 10/30/2016.
 */

public class DetailedTweetFragment extends DialogFragment {
    public final static String TweetParcelKey = "tweetkey";

    public static DetailedTweetFragment newInstance(Tweet tweet) {
        DetailedTweetFragment frag = new DetailedTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(TweetParcelKey, Parcels.wrap(tweet));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_tweet_detail, container);
    }

    TextView mScreenName;
    TextView mTwitHandle;
    TextView mCreatedAt;
    ImageView mProfileImage;
    LinkifiedTextView mTweetText;
    ImageView mCloseImage;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProfileImage = (ImageView) view.findViewById(R.id.ivFragTweetImage);
        mScreenName = (TextView) view.findViewById(R.id.tvScreenName);
        mTwitHandle = (TextView) view.findViewById(R.id.tvHandle);
        mTweetText = (LinkifiedTextView) view.findViewById(R.id.etFragTweetText);
        mCreatedAt = (TextView) view.findViewById(R.id.tvTweetCreated);
        mCloseImage = (ImageView) view.findViewById(R.id.ivFragClose);
        mCloseImage.setOnClickListener(closeListener);
        Tweet postedTweet = Parcels.unwrap(getArguments().getParcelable(TweetParcelKey));
        Toast.makeText(getActivity(), postedTweet.getBody(), Toast.LENGTH_SHORT).show();
        String img = postedTweet.getUser().getProfileImageUrl();
        Glide.with(this.getActivity()).load(img)
                .bitmapTransform(new RoundedCornersTransformation(this.getActivity(), 3, 3))
                .into(mProfileImage);
        mTweetText.setText(postedTweet.getBody());
        mScreenName.setText(postedTweet.getUser().getName());
        mTwitHandle.setText("@" + postedTweet.getUser().getScreenName());

        mCreatedAt.setText(getFormattedDate(postedTweet.getCreatedAt()));
    }

    View.OnClickListener closeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        // Call super onResume after sizing
        super.onResume();

    }

    private String getFormattedDate (String inDate) {
        Calendar date = Calendar.getInstance();
        String createdAt = "";
        SimpleDateFormat insdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        SimpleDateFormat outsdf = new SimpleDateFormat("h:mm a - dd MMM yyyy");
        try {
            date.setTime(insdf.parse(inDate));
            createdAt = outsdf.format(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createdAt;
    }
}
