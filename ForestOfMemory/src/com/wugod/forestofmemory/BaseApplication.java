package com.wugod.forestofmemory;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.impl.CommunityFactory;

import android.app.Application;

public class BaseApplication extends Application {
	CommunitySDK mCommSDK;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mCommSDK = CommunityFactory.getCommSDK(this);
		// ��ʼ��sdk���봫��ApplicationContext
		mCommSDK.initSDK(this);
	}

}
