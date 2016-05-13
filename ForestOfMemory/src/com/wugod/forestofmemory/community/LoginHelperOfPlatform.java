package com.wugod.forestofmemory.community;

import android.util.Log;
import android.view.View;

import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.login.LoginListener;
import com.umeng.commm.ui.fragments.CommunityMainFragment;
import com.wugod.forestofmemory.BaseApplication;
import com.wugod.forestofmemory.R;

public class LoginHelperOfPlatform {
	public String TAG = "LoginHelperOfPlatform";

	public void login(String name, String id, LoginListener loginListener) {
		// TODO Auto-generated method stub
		BaseApplication.instance.getmCommSDK().loginToUmengServerBySelfAccount(
				BaseApplication.instance.getApplicationContext(), name, id,
				loginListener);
	}
}
