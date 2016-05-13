package com.umeng.commm.ui.fragments;

import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.comm.core.nets.uitls.NetworkUtils;
import com.umeng.comm.core.utils.CommonUtils;
import com.umeng.comm.core.utils.DeviceUtils;
import com.umeng.comm.core.utils.Log;
import com.umeng.comm.core.utils.ResFinder;
import com.umeng.commm.ui.activities.PostFeedActivity;
import com.umeng.commm.ui.activities.SearchActivity;
import com.umeng.common.ui.presenter.impl.HottestFeedPresenter;
import com.umeng.common.ui.util.BroadcastUtils;

import java.util.List;

/**
 * Created by umeng on 12/1/15.
 */
public class HotFeedsFragment extends PostBtnAnimFragment<HottestFeedPresenter> {

    HottestFeedPresenter mHottestFeedPresenter;
    private TextView button1,button2,button3,button4;
    @Override
    protected void initWidgets() {
        super.initWidgets();
//        mPostBtn.setVisibility(View.GONE);
        mPostBtn.setOnClickListener(
//                new Listeners.LoginOnViewClickListener() {
//            @Override
//            protected void doAfterLogin(View v) {
//                gotoPostFeedActivity();
//            }
//        })
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!CommonUtils.isLogin(getActivity())) {
                            CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                                @Override
                                public void onStart() {
                                    if (getActivity()!=null&&!getActivity().isFinishing()) {
                                        mProcessDialog.show();
                                    }
                                }

                                @Override
                                public void onComplete(int stCode, CommUser userInfo) {
                                    if (getActivity()!=null&&!getActivity().isFinishing()) {
                                        mProcessDialog.dismiss();
                                    }
                                    if (stCode == 0) {
                                        gotoPostFeedActivity();
                                    }
                                }
                            });
                        } else {

                            gotoPostFeedActivity();
                        }
                    }
                })
        ;
    }
    /**
     * 跳转至发送新鲜事页面</br>
     */
    private void gotoPostFeedActivity() {
        Intent postIntent = new Intent(getActivity(), PostFeedActivity.class);
        startActivity(postIntent);
    }
    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();
        View headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_search_header_view"), null);
        View tipView = headerView.findViewById(ResFinder.getId("umeng_comm_comment_send_button"));
        FrameLayout.LayoutParams tipViewParams = (FrameLayout.LayoutParams)tipView.getLayoutParams();
        tipViewParams.topMargin = 0;

        headerView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CommonUtils.visitNum == 0){
                    if (CommonUtils.isLogin(getActivity())){
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        getActivity().startActivity(intent);
                    }
                    else {
                        CommunitySDKImpl.getInstance().login(getActivity(), new LoginListener() {
                            @Override
                            public void onStart() {
                                if (getActivity()!=null&&!getActivity().isFinishing()){
                                    mProcessDialog.show();
                                }
                            }

                            @Override
                            public void onComplete(int stCode, CommUser userInfo) {
                                if (getActivity()!=null&&!getActivity().isFinishing()){
                                    mProcessDialog.dismiss();
                                }
                                if (stCode == 0) {
                                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                                    getActivity().startActivity(intent);
                                }
                            }
                        });
                    }
                }else {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    getActivity().startActivity(intent);
                }
            }
        });
        mFeedsListView.addHeaderView(headerView);
        initSwitchView();
    }
    @Override
    protected void showPostButtonWithAnim() {
        AlphaAnimation showAnim = new AlphaAnimation(0.5f, 1.0f);
        showAnim.setDuration(500);

        mPostBtn.setVisibility(View.VISIBLE);
        mPostBtn.startAnimation(showAnim);
    }
    public void initSwitchView(){
        View headerView = LayoutInflater.from(getActivity()).inflate(
                ResFinder.getLayout("umeng_comm_switch_button"), null);

        button1 = (TextView)headerView.findViewById(ResFinder.getId("umeng_switch_button_one"));
        button2 = (TextView)headerView.findViewById(ResFinder.getId("umeng_switch_button_two"));
        button3 = (TextView)headerView.findViewById(ResFinder.getId("umeng_switch_button_three"));
        button4 = (TextView)headerView.findViewById(ResFinder.getId("umeng_switch_button_four"));
        button4.setSelected(true);
        button1.setOnClickListener(switchListener);
        button2.setOnClickListener(switchListener);
        button3.setOnClickListener(switchListener);
        button4.setOnClickListener(switchListener);
        mLinearLayout.addView(headerView, 0);
    }
    private View.OnClickListener switchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == ResFinder.getId("umeng_switch_button_one")){
                button1.setSelected(true);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(false);
                if(NetworkUtils.isConnectedToNetwork(getActivity())){
                    ((HottestFeedPresenter)mPresenter).loadDataFromServer(1);
                }else{
                    ((HottestFeedPresenter)mPresenter).loadDataFromDB(1);
                }
            }else if(view.getId() == ResFinder.getId("umeng_switch_button_two")){
                button1.setSelected(false);
                button2.setSelected(true);
                button3.setSelected(false);
                button4.setSelected(false);
                if(NetworkUtils.isConnectedToNetwork(getActivity())){
                    ((HottestFeedPresenter)mPresenter).loadDataFromServer(3);
                }else{
                    ((HottestFeedPresenter)mPresenter).loadDataFromDB(3);
                }
            }else if(view.getId() == ResFinder.getId("umeng_switch_button_three")){
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(true);
                button4.setSelected(false);
                if(NetworkUtils.isConnectedToNetwork(getActivity())){
                    ((HottestFeedPresenter)mPresenter).loadDataFromServer(7);
                }else{
                    ((HottestFeedPresenter)mPresenter).loadDataFromDB(7);
                }
            }else if(view.getId() == ResFinder.getId("umeng_switch_button_four")){
                button1.setSelected(false);
                button2.setSelected(false);
                button3.setSelected(false);
                button4.setSelected(true);
                if(NetworkUtils.isConnectedToNetwork(getActivity())){
                    ((HottestFeedPresenter)mPresenter).loadDataFromServer(30);
                }else{
                    ((HottestFeedPresenter)mPresenter).loadDataFromDB(30);
                }
            }
        }
    };
    @Override
    protected HottestFeedPresenter createPresenters() {
        mHottestFeedPresenter = new HottestFeedPresenter(this);
        return mHottestFeedPresenter;
    }
    @Override
    protected void registerBroadcast() {
        // 注册广播接收器
        BroadcastUtils.registerUserBroadcast(getActivity(), mReceiver);
       BroadcastUtils.registerFeedBroadcast(getActivity(), mReceiver);
        BroadcastUtils.registerFeedUpdateBroadcast(getActivity(), mReceiver);
    }
    @Override
    public List<FeedItem> getBindDataSource() {
        return super.getBindDataSource();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
    @Override
    protected void postFeedComplete(FeedItem feedItem) {

    }
    @Override
    protected void deleteFeedComplete(FeedItem feedItem) {
        mFeedLvAdapter.getDataSource().remove(feedItem);
        mFeedLvAdapter.notifyDataSetChanged();
        updateForwardCount(feedItem, -1);
        Log.d(getTag(), "### 删除feed");
    }
    @Override
    public void clearListView() {
        super.clearListView();
    }

}
