/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.umeng.commm.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.comm.core.utils.ResFinder.ResType;
import com.umeng.comm.core.utils.ToastMsg;
import com.umeng.common.ui.dialogs.ActionDialog;
import com.umeng.common.ui.mvpview.MvpFeedDetailActivityView;
import com.umeng.common.ui.presenter.impl.FeedDetailActivityPresenter;


/**
 * Feed详情页的举报、删除、拷贝Dialog
 */
public class FeedActionDialog extends ActionDialog {

    FeedDetailActivityPresenter mPresenter;

    public FeedActionDialog(Context context) {
        super(context);
        mPresenter = new FeedDetailActivityPresenter();
        mPresenter.attach(context);
    }

    public void attachView(MvpFeedDetailActivityView view) {
        mPresenter.setActivityView(view);
    }

    public void setFeedId(String feedId) {
        mFeedItem = new FeedItem();
        mFeedItem.id = feedId;
        this.setFeedItem(mFeedItem);
    }

    @Override
    protected void initViewClickListeners() {
        super.initViewClickListeners();
        mCopyView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                copyToClipboard();
                dismiss();
            }
        });

        mDeleteView.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void onStart(View v) {
                dismiss();
            }
            @Override
            protected void doAfterLogin(View v) {
                dismiss();
                mPresenter.showDeleteConfirmDialog();
            }
        });

        mSaveView.setOnClickListener(new Listeners.LoginOnViewClickListener() {
            @Override
            protected void onStart(View v) {
                dismiss();
            }
            @Override
            public void doAfterLogin(View v) {
                dismiss();
                if(mFeedItem.creator == null || mFeedItem.topics == null){
                    return;
                }
                mPresenter.forbidUser(mFeedItem.creator,
                        mFeedItem.topics.get(0).id);
            }
        });
        updateViewState();
    }

    private void updateViewState(){
        // forbid
        mShareView.setVisibility(View.VISIBLE);

//        if(mFeedItem != null && mFeedItem.ban_user){
//            mSaveView.setVisibility(View.VISIBLE);
//            mFavoriteTextView.setText(ResFinder.getString("umeng_comm_forbid_user"));
//        }else{
            mSaveView.setVisibility(View.GONE);
//        }

        if (mSaveView.getVisibility() == View.GONE) {
            mShareView.setBackgroundResource(ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_more_radius_top"));
        } else {
            mShareView.setBackgroundResource(ResFinder.getResourceId(ResType.DRAWABLE, "umeng_comm_more_radius_none"));
        }

        // 举报
        // 自己不能举报自己.[注意：考虑来自于推送的情况]
        if (!isReportable()){
            mReportUser.setVisibility(View.GONE);
        }else{
            mReportUser.setVisibility(View.VISIBLE);
        }

        // 删除
        if (!isDeleteable()) {
            mDeleteView.setVisibility(View.GONE);
        } else {
            mDeleteView.setVisibility(View.VISIBLE);
        }

        // 举报内容
        if(isDeleteable()){
            mReportView.setVisibility(View.GONE);
        }else if(isReportable()){
            mReportView.setVisibility(View.VISIBLE);
        }

        mSetRecommened.setVisibility(View.GONE);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateViewState();
    }

    public void setFeedItem(FeedItem feedItem) {
        mFeedItem = feedItem;
        mPresenter.setFeedItem(feedItem);
        updateViewState();
    }

    private boolean isReportable() {
        CommUser loginedUser = CommConfig.getConfig().loginedUser;
        if (mFeedItem != null && !loginedUser.id.equals(mFeedItem.creator.id)){
            return true;
        }
        else{
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void copyToClipboard() {
        if(mFeedItem == null){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipData data = ClipData.newPlainText("feed_text", mFeedItem.text);
            android.content.ClipboardManager mClipboard = (android.content.ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            mClipboard.setPrimaryClip(data);
        } else {
            android.text.ClipboardManager mClipboard = (android.text.ClipboardManager) getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            mClipboard.setText(mFeedItem.text);
        }
    }

    /**
     * 是否可删除该feed。可删除的条件是自己的feed、管理员有删除内容的权限</br>
     * 
     * @return
     */
    private boolean isDeleteable() {
        CommUser loginedUser = CommConfig.getConfig().loginedUser;
        boolean deleteable = mFeedItem != null && loginedUser.id.equals(mFeedItem.creator.id); // 自己的feed情况
        boolean hasDeletePermission = mFeedItem != null && mFeedItem.permission >= Constants.PERMISSION_CODE_DELETE;
        return deleteable || hasDeletePermission;
    }

    @Override
    protected void report() {
        String loginedUid = CommConfig.getConfig().loginedUser.id;
        if (mFeedItem.creator.id.equals(loginedUid)) {
            ToastMsg.showShortMsgByResName("umeng_comm_do_not_spam_yourself_content");
            return;
        }
        mPresenter.showReportConfirmDialog();
    }

    @Override
    protected void reportUser() {
        mPresenter.showReportUserConfirmDialog();
    }
}
