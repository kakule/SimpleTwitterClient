package com.codepath.apps.simpletwitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.models.User;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created on 11/5/2016.
 */

public class UserHeaderFragment extends Fragment {

    ImageView profilePic;
    TextView userName;
    TextView tagLine;
    TextView followersCount;
    TextView followingCount;

    public static UserHeaderFragment newInstance(User user) {
        UserHeaderFragment usrHeadFrag = new UserHeaderFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", Parcels.wrap(user));
        usrHeadFrag.setArguments(args);
        return usrHeadFrag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_usertimeline_header, parent, false);
        profilePic = (ImageView) v.findViewById(R.id.ivUserDetailPic);
        userName = (TextView) v.findViewById(R.id.tvUserDetailName);
        tagLine = (TextView) v.findViewById(R.id.tvUserDetailTagline);
        followersCount = (TextView) v.findViewById(R.id.tvfollowersCount);
        followingCount = (TextView) v.findViewById(R.id.tvfollowingCount);

        User currUser = Parcels.unwrap(getArguments().getParcelable("user"));
        userName.setText(currUser.getName());
        if (currUser.getTagLine() != null)
            tagLine.setText(currUser.getTagLine());
        followersCount.setText(currUser.getFollowersCount() + " Followers");
        followingCount.setText(currUser.getFriendsCount() + " Following");
        //Also set profile pic
        Glide.with(this.getActivity()).load(currUser.getProfileImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(this.getActivity(), 3, 3))
                .into(profilePic);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
