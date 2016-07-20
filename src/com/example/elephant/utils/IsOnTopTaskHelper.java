package com.example.elephant.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

public class IsOnTopTaskHelper {

	public static String getTopActivityName(Context context){
		String topActivityName=null;
		 ActivityManager activityManager =
		(ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE )) ;
	     List<RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1) ;
	     if(runningTaskInfos != null){
	    	 ComponentName f=runningTaskInfos.get(0).topActivity;
	    	 String topActivityClassName=f.getClassName();
	    	 String temp[]=topActivityClassName.split("\\.");
	    	 topActivityName=temp[temp.length-1];
	    	 System.out.println("topActivityName="+topActivityName);
	     }
	     return topActivityName;
	}
}
