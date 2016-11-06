package com.codepath.apps.simpletwitterclient.fragments;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.models.Draft;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created on 10/29/2016.
 */

public class ComposeDialogFragment extends DialogFragment
        implements TweetAlertDialogFragment.AlertDialogListener {

    public interface PostTweetDialogListener {
        void onFinishDialog(String inputText);
    }


    public static String profileImageKey = "profilekey";
    private final int MAX_CHARACTERS = 140;
    private ImageView mProfileImage;
    private EditText mComposeText;
    private TextView mCharactersLeft;
    private Button mTweetButton;
    private ImageView mCloseImage;
    Draft draft;
    public ComposeDialogFragment() {

    }

    public static ComposeDialogFragment newInstance(String img) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString(profileImageKey, img);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_compose_tweet, container);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProfileImage = (ImageView) view.findViewById(R.id.ivFragComposeImage);
        mComposeText = (EditText) view.findViewById(R.id.etFragComposeText);
        mComposeText.addTextChangedListener(characterwatcher);
        mCharactersLeft = (TextView) view.findViewById(R.id.tvCharactersLeft);
        mTweetButton = (Button) view.findViewById(R.id.btfragSave);
        mTweetButton.setOnClickListener(tweetListener);
        mCloseImage = (ImageView) view.findViewById(R.id.ivFragClose);
        mCloseImage.setOnClickListener(closeListener);
        String img = getArguments().getString(profileImageKey);
        Glide.with(this.getActivity()).load(getArguments().getString(profileImageKey))
                .bitmapTransform(new RoundedCornersTransformation(this.getActivity(), 3, 3))
                .into(mProfileImage);
        mCharactersLeft.setText(Integer.toString(MAX_CHARACTERS));
        if (Draft.recentItems() != null) {
            draft = Draft.recentItems();
            mComposeText.setText(draft.getDraft());
            mComposeText.setSelection(draft.getDraft().length());
            draft.delete();
        }
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();

    }

    TextWatcher characterwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int currentLength = start + count;
            if (currentLength > 0) {
                mCharactersLeft.setText(Integer.toString(MAX_CHARACTERS - currentLength));
                mTweetButton.setEnabled(true);
            } else {
                mTweetButton.setEnabled(false);
                mCharactersLeft.setText(Integer.toString(MAX_CHARACTERS));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    View.OnClickListener closeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    View.OnClickListener tweetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PostTweetDialogListener tweetpost = (PostTweetDialogListener) getActivity();
            String tweetext = mComposeText.getText().toString().trim();
            tweetpost.onFinishDialog(tweetext);
            dismiss();
        }
    };

    @Override
    public void onCancel(DialogInterface dialog) {

        if (!mComposeText.getText().toString().trim().isEmpty()) {
            FragmentManager fm = getFragmentManager();
            TweetAlertDialogFragment alertDialogFragment =
                    TweetAlertDialogFragment.newInstance("You are exiting an unfinished tweet!!");
            // SETS the target fragment for use later when sending results
            alertDialogFragment.setTargetFragment(ComposeDialogFragment.this, 300);
            alertDialogFragment.show(fm, "fragment_alert");
            }
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishAlertDialog(boolean choice) {
        if (choice) {
            draft = new Draft(mComposeText.getText().toString().trim());
            draft.save();
        }

    }
}
