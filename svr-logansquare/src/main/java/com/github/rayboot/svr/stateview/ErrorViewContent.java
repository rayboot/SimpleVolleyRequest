package com.github.rayboot.svr.stateview;

import android.text.TextUtils;

import com.github.rayboot.svr.R;

import java.util.Map;

/**
 * Created by rayboot on 15/4/29.
 */
public class ErrorViewContent {
    private int imgRes;
    private int titleRes;
    private String subTitle;
    private int btnTitleRes;
    private int btnRes;
    private int state;

    public static ErrorViewContent getContentObj(int state) {
        Map<Integer, Integer> mCodes = HttpStatusCodes.getCodesMap();
        int title = 0;
        if (mCodes.containsKey(state)) {
            title = mCodes.get(state);
        }
        if (state > 0) {
            title = R.string.state_404;
        }
        switch (state) {
            case HttpStatusCodes.FINISH:
                return null;
            case HttpStatusCodes.NO_CONNECT:
                return new ErrorViewContent(state, R.drawable.state_no_network, title, null, R.string.state_btn_retry, 0);
            case HttpStatusCodes.NO_MORE_INFO:
                return new ErrorViewContent(state, R.drawable.state_no_more_info, title, null, 0, 0);
            case HttpStatusCodes.NO_MORE_DATA:
                return new ErrorViewContent(state, R.drawable.state_no_more_info, title, null, 0, 0);
            case HttpStatusCodes.GET_ALL_MESSAGE:
                return new ErrorViewContent(state, R.drawable.state_select_all_time, title, null, 0, 0);
            case HttpStatusCodes.NO_MESSAGE:
                return new ErrorViewContent(state, R.drawable.state_no_msg, title, null, 0, 0);
            case HttpStatusCodes.LOADING:
                return new ErrorViewContent(state, R.drawable.anim_state_loading, title, null, 0, 0);
            case HttpStatusCodes.PARSE_ERROR:
                return new ErrorViewContent(state, R.drawable.state_no_more_info, title, null, 0, 0);
            default:
                return new ErrorViewContent(state, R.drawable.state_404, title, null, R.string.state_btn_retry, 0);
        }
    }

    public ErrorViewContent(int state, int imgRes, int titleRes, String subTitle, int btnTitleRes, int btnRes) {
        this.state = state;
        this.titleRes = titleRes;
        this.imgRes = imgRes;
        this.subTitle = subTitle;
        this.btnTitleRes = btnTitleRes;
        this.btnRes = btnRes;
    }

    public boolean haveTitle() {
        return titleRes > 0;
    }

    public boolean haveSubTitle() {
        return !TextUtils.isEmpty(subTitle);
    }

    public boolean haveImg() {
        return imgRes > 0;
    }

    public boolean haveButton() {
        return btnTitleRes > 0 || btnRes > 0;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getBtnTitleRes() {
        return btnTitleRes;
    }

    public void setBtnTitleRes(int btnTitleRes) {
        this.btnTitleRes = btnTitleRes;
    }

    public int getBtnRes() {
        return btnRes;
    }

    public void setBtnRes(int btnRes) {
        this.btnRes = btnRes;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
