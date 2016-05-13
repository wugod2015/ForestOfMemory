package com.wugod.forestofmemory;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;

import android.app.Application;


public class BaseApplication extends Application {
	CommunitySDK mCommSDK;
	public static BaseApplication instance;

	public CommunitySDK getmCommSDK() {
		if (mCommSDK == null) {
			mCommSDK = CommunityFactory.getCommSDK(this);
			// ��ʼ��sdk���봫��ApplicationContext
			mCommSDK.initSDK(this);
		}
		return mCommSDK;
	}

	public void setmCommSDK(CommunitySDK mCommSDK) {
		this.mCommSDK = mCommSDK;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;

		mCommSDK = CommunityFactory.getCommSDK(this);
		// ��ʼ��sdk���봫��ApplicationContext
		mCommSDK.initSDK(this);
	}

}
