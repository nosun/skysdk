package com.skyware.sdk.thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.skyware.sdk.consts.SDKConst;

public class ThreadPoolManager {
	
	private static ThreadPoolManager mInstance = new ThreadPoolManager();
	private ThreadPoolManager(){
		mThreadPool =  new ScheduledThreadPoolExecutor(coreThreadNum);
	}
	public static ThreadPoolManager getInstance() {
		return mInstance;
	}
	
	private int coreThreadNum = SDKConst.THREAD_POOL_CORE_INITIAL_NUM;

	/** 线程池 */
	private ScheduledThreadPoolExecutor mThreadPool;

    public synchronized void addCoreThread(int num){
    	if (mThreadPool == null) {
    		mThreadPool = new ScheduledThreadPoolExecutor(coreThreadNum);
		}
    	coreThreadNum += num;
    	mThreadPool.setCorePoolSize(coreThreadNum);
    }
	
	public synchronized ScheduledThreadPoolExecutor getThreadPool(){
		if (mThreadPool == null) {
    		mThreadPool = new ScheduledThreadPoolExecutor(coreThreadNum);
		}
		return mThreadPool;
	}
	
	public void finallize() {
		mThreadPool.shutdownNow();
		mThreadPool = null;
		coreThreadNum = 0;
	}
}
