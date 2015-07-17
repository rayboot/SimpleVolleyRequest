/*
 * Copyright (C) 2015 Ihsan Isik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rayboot.svr.stateview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.rayboot.svr.R;

import java.util.Map;


/**
 * @author Ihsan Isik
 *         A custom view that displays an error image, a title, and a subtitle given an HTTP status
 *         code. It can be used for various other purposes like displaying other kinds of errors or
 *         just messages with images.
 */
public class StateView extends LinearLayout {
    private ImageView mErrorImageView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private TextView mRetryButton;

    private RetryListener mListener;
    private boolean mUseIntrinsicAnimation = false;

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.StateView, 0, 0);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.error_view_layout, this, true);

        mErrorImageView = (ImageView) findViewById(R.id.error_image);
        mTitleTextView = (TextView) findViewById(R.id.error_title);
        mSubtitleTextView = (TextView) findViewById(R.id.error_subtitle);
        mRetryButton = (TextView) findViewById(R.id.error_retry);

        int imageRes;

        String title;
        int titleColor;

        String subtitle;
        int subtitleColor;

        boolean showTitle;
        boolean showSubtitle;
        boolean showRetryButton;

        String retryButtonText;
        int retryButtonBackground;
        int retryButtonTextColor;

        try {
            imageRes = a.getResourceId(R.styleable.StateView_ev_errorImage, R.drawable.anim_state_loading);
            title = a.getString(R.styleable.StateView_ev_title);
            titleColor = a.getColor(R.styleable.StateView_ev_titleColor,
                    getResources().getColor(R.color.error_view_text));
            subtitle = a.getString(R.styleable.StateView_ev_subtitle);
            subtitleColor = a.getColor(R.styleable.StateView_ev_subtitleColor,
                    getResources().getColor(R.color.error_view_text_light));
            showTitle = a.getBoolean(R.styleable.StateView_ev_showTitle, true);
            showSubtitle = a.getBoolean(R.styleable.StateView_ev_showSubtitle, false);
            showRetryButton = a.getBoolean(R.styleable.StateView_ev_showRetryButton, false);
            retryButtonText = a.getString(R.styleable.StateView_ev_retryButtonText);
            retryButtonBackground = a.getResourceId(R.styleable.StateView_ev_retryButtonBackground,
                    R.drawable.selector_state_btn);
            retryButtonTextColor = a.getColor(R.styleable.StateView_ev_retryButtonTextColor,
                    getResources().getColor(R.color.error_view_text_dark));

            if (imageRes != 0)
                setImageResource(imageRes);

            if (title != null)
                setTitle(title);

            if (subtitle != null)
                setSubtitle(subtitle);

            if (retryButtonText != null)
                mRetryButton.setText(retryButtonText);

            if (!showTitle)
                mTitleTextView.setVisibility(GONE);

            if (!showSubtitle)
                mSubtitleTextView.setVisibility(GONE);

            if (!showRetryButton)
                mRetryButton.setVisibility(GONE);

            mTitleTextView.setTextColor(titleColor);
            mSubtitleTextView.setTextColor(subtitleColor);

            mRetryButton.setTextColor(retryButtonTextColor);
            mRetryButton.setBackgroundResource(retryButtonBackground);

            if (mErrorImageView.getDrawable() instanceof AnimationDrawable) {
                mUseIntrinsicAnimation = true;
                ((AnimationDrawable) mErrorImageView.getDrawable()).start();
            }
        } finally {
            a.recycle();
        }

        mRetryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) mListener.onRetry();
            }
        });
    }

    /**
     * Attaches a listener that to the view that reports retry events.
     *
     * @param listener {@link} to be notified when a retry
     *                 event occurs.
     */
    public void setOnRetryListener(RetryListener listener) {
        this.mListener = listener;
    }

    /**
     * Sets error subtitle to the description of the given HTTP status code
     *
     * @param content HTTP status code
     */
    public void setState(ErrorViewContent content) {
        if (mUseIntrinsicAnimation && mErrorImageView.getDrawable() != null) {
            ((AnimationDrawable) mErrorImageView.getDrawable()).stop();
        }
        if (content == null) {
            this.setVisibility(GONE);
            return;
        }
        showImageView(content.haveImg());
        showTitle(content.haveTitle());
        showSubtitle(content.haveSubTitle());
        showRetryButton(content.haveButton());

        if (isImageViewVisible()) {
            mErrorImageView.setImageResource(content.getImgRes());
            mUseIntrinsicAnimation = mErrorImageView.getDrawable() instanceof AnimationDrawable;
            if (mUseIntrinsicAnimation) {
                ((AnimationDrawable) mErrorImageView.getDrawable()).start();
            }
        }

        if (isTitleVisible()) {
            setTitleTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f);
            setTitleLineSpacing(0.0f, 1.0f);
            setTitle(content.getTitleRes());
        }

        if (isSubtitleVisible()) {
            setSubtitle(content.getSubTitle());
        }

        if (isRetryButtonVisible()) {
            setRetryButtonText(content.getBtnTitleRes());
            mRetryButton.setBackgroundResource(content.getBtnRes());
        }

        if (content.getState() > 0) {
            Map<Integer, Integer> mCodes = HttpStatusCodes.getCodesMap();
            if (mCodes.containsKey(content.getState())) {
                showSubtitle(true);
                setSubtitle(content.getState() + " " + getResources().getString(mCodes.get(content.getState())));
            }
        }
    }

    /**
     * Sets error image to a given drawable resource
     *
     * @param res drawable resource.
     */
    public void setImageResource(int res) {
        mErrorImageView.setImageResource(res);
    }

    /**
     * Sets the error image to a given {@link Drawable}.
     *
     * @param drawable {@link Drawable} to use as error image.
     */
    public void setImageDrawable(Drawable drawable) {
        mErrorImageView.setImageDrawable(drawable);
    }

    /**
     * Sets the error image to a given {@link Bitmap}.
     *
     * @param bitmap {@link Bitmap} to use as error image.
     */
    public void setImageBitmap(Bitmap bitmap) {
        mErrorImageView.setImageBitmap(bitmap);
    }

    /**
     * Sets the error title to a given {@link String}.
     *
     * @param text {@link String} to use as error title.
     */
    public void setTitle(String text) {
        mTitleTextView.setText(text);
    }

    /**
     * Sets the error title to a given string resource.
     *
     * @param res string resource to use as error title.
     */
    public void setTitle(int res) {
        mTitleTextView.setText(res);
    }

    /**
     * Returns the current title string.
     */
    public String getTitle() {
        return mTitleTextView.getText().toString();
    }

    /**
     * Sets the error title text to a given color.
     *
     * @param res color resource to use for error title text.
     */
    public void setTitleColor(int res) {
        mTitleTextView.setTextColor(res);
    }

    /**
     * Sets the error title text to a given text size.
     *
     * @param unit TypedValue.COMPLEX_UNIT_SP, TypedValue.COMPLEX_UNIT_DIP.
     * @param size text size.
     */
    public void setTitleTextSize(int unit, float size) {
        mTitleTextView.setTextSize(unit, size);
    }

    /**
     * Sets line spacing for this TextView.  Each line will have its height
     * multiplied by <code>mult</code> and have <code>add</code> added to it.
     *
     * @param add android.R.styleable#TextView_lineSpacingExtra
     * @param mult android.R.styleable#TextView_lineSpacingMultiplie
     */
    public void setTitleLineSpacing(float add, float mult) {
        mTitleTextView.setLineSpacing(add, mult);
    }

    /**
     * Returns the current title text color.
     */
    public int getTitleColor() {
        return mTitleTextView.getCurrentTextColor();
    }

    /**
     * Sets the error subtitle to a given {@link String}.
     *
     * @param exception {@link String} to use as error subtitle.
     */
    public void setSubtitle(String exception) {
        mSubtitleTextView.setText(exception);
    }

    /**
     * Sets the error subtitle to a given string resource.
     *
     * @param res string resource to use as error subtitle.
     */
    public void setSubtitle(int res) {
        mSubtitleTextView.setText(res);
    }

    /**
     * Returns the current subtitle.
     */
    public String getSubtitle() {
        return mSubtitleTextView.getText().toString();
    }

    /**
     * Sets the error subtitle text to a given color
     *
     * @param res color resource to use for error subtitle text.
     */
    public void setSubtitleColor(int res) {
        mSubtitleTextView.setTextColor(res);
    }

    /**
     * Returns the current subtitle text color.
     */
    public int getSubtitleColor() {
        return mSubtitleTextView.getCurrentTextColor();
    }

    /**
     * Sets the retry button's text to a given {@link String}.
     *
     * @param text {@link String} to use as retry button text.
     */
    public void setRetryButtonText(String text) {
        mRetryButton.setText(text);
    }

    /**
     * Sets the retry button's text to a given string resource.
     *
     * @param res string resource to be used as retry button text.
     */
    public void setRetryButtonText(int res) {
        mRetryButton.setText(res);
    }

    /**
     * Returns the current retry button text.
     */
    public String getRetryButtonText() {
        return mRetryButton.getText().toString();
    }

    /**
     * Shows or hides the error title
     */
    public void showTitle(boolean show) {
        mTitleTextView.setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * Indicates whether the title is currently visible.
     */
    public boolean isTitleVisible() {
        return mTitleTextView.getVisibility() == VISIBLE;
    }

    /**
     * Shows or hides error subtitle.
     */
    public void showSubtitle(boolean show) {
        mSubtitleTextView.setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * Indicates whether the subtitle is currently visible.
     */
    public boolean isSubtitleVisible() {
        return mSubtitleTextView.getVisibility() == VISIBLE;
    }

    /**
     * Shows or hides the retry button.
     */
    public void showRetryButton(boolean show) {
        mRetryButton.setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * Indicates whether the retry button is visible.
     */
    public boolean isRetryButtonVisible() {
        return mRetryButton.getVisibility() == VISIBLE;
    }

    /**
     * Shows or hides the image view.
     */
    public void showImageView(boolean show) {
        mErrorImageView.setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * Indicates whether the retry button is visible.
     */
    public boolean isImageViewVisible() {
        return mErrorImageView.getVisibility() == VISIBLE;
    }

    public interface RetryListener {
        public void onRetry();
    }
    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public TextView getSubtitleTextView() {
        return mSubtitleTextView;
    }

    public TextView getRetryButton() {
        return mRetryButton;
    }

    public ImageView getStateImageView() {
        return mErrorImageView;
    }
}