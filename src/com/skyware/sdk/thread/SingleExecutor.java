package com.skyware.sdk.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SingleExecutor {
	
	private ScheduledExecutorService executor;
	
	public SingleExecutor() {
	}
	
	public <T> Future<T> submit(Callable<T> callable) {
		if(executor == null)
			executor = Executors.newSingleThreadScheduledExecutor();
		return executor.submit(callable);
	}
	
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		if(executor == null)
			executor = Executors.newSingleThreadScheduledExecutor();
		return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}
	
	public <T> ScheduledFuture<T> schedule(Callable<T> callable, long delay, TimeUnit unit) {
		if(executor == null)
			executor = Executors.newSingleThreadScheduledExecutor();
		return executor.schedule(callable, delay, unit);
	}
	
	/**
	 * 停止执行器线程
	 */
	public void dispose() {
		if(executor != null) {
			executor.shutdownNow();
			executor = null;			
		}
	}

}
