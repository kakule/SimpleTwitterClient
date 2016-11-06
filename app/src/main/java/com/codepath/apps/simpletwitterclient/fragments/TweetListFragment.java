package com.codepath.apps.simpletwitterclient.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.adapters.RecycleTweetsAdapter;
import com.codepath.apps.simpletwitterclient.adapters.SpacesItemDecoration;
import com.codepath.apps.simpletwitterclient.interfaces.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitterclient.models.Tweet;
import com.codepath.apps.simpletwitterclient.models.User;

import java.util.ArrayList;

public class TweetListFragment extends Fragment {

    protected ArrayList<Tweet> tweets;
    protected RecycleTweetsAdapter aTweets;
    protected RecyclerView rvTweets;
    // Store a member variable for the listener
    protected EndlessRecyclerViewScrollListener scrollListener;
    //decoration for recycleview
    SpacesItemDecoration decoration;
    protected SwipeRefreshLayout swipeContainer;

    public interface TimelineListener {
        void onTweetClick(User user, int type);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweets);
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(refreshSwipe);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        // Set layout manager to position the items
        rvTweets.setLayoutManager(linearLayoutManager);
        decoration = new SpacesItemDecoration(5);
        rvTweets.addItemDecoration(decoration);
        //connect adapter to recycleview
        rvTweets.setAdapter(aTweets);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                return loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        return v;
    }

    //creation lifecycle event
    @Override
    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create the arraylist (data source)
        tweets = new ArrayList<>();
        //construct the adapter from data source
        aTweets = new RecycleTweetsAdapter(getActivity(), tweets);

        //add click listener to adapter
        aTweets.setOnItemClickListener(new RecycleTweetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int type) {
                if(type == RecycleTweetsAdapter.PROFILE_VIEW) {
                    Toast.makeText(getActivity(), "Image clicked", Toast.LENGTH_SHORT).show();
                    sendRequestToActivity (tweets.get(position).getUser(), type);
                } else {
                    Toast.makeText(getActivity(), "Non Image clicked", Toast.LENGTH_SHORT).show();
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    DetailedTweetFragment detailFrag =
                            DetailedTweetFragment.newInstance(tweets.get(position));
                    detailFrag.show(fm, "detailfragment");
                }
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener refreshSwipe = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh() {
            //refresh the view
            refreshView();
        }
    };

    public void addAll (ArrayList<Tweet> intweets, boolean clear) {
        if (clear) {
            tweets.clear();
        }
        tweets.addAll(intweets);
        aTweets.notifyDataSetChanged();
    }

    public void insertTop (Tweet postedTweet) {
        tweets.add(0, postedTweet);
        aTweets.notifyItemInserted(0);
    }

    public void refreshView () {
        //Implement in child
    }

    public void postTweet(String tweetStr) {
        //Implement in child
    }
    public boolean loadNextDataFromApi(int offset) {
        return true;
    }

    public void sendRequestToActivity (User user, int type) {
        // Pass click request to activity
        TimelineListener listener = (TimelineListener) getActivity();
        listener.onTweetClick(user, type);
    }

}
