package com.example.elephant.im.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShowChatMsgListView {

	private static String accountName;
	private static String content;
	private static long postTime;
	public static boolean isInit=false;//该类是否已经初始化过
	
	public ShowChatMsgListView() {
		
		isInit=true;
		
		
		
	}
	
	/**
	 * 设置列表消息并将消息保存到数据库
	 * @param fromCustomUserID 
	 * @param text
	 * @param serverActionTime
	 */
	public static void setData(String fromCustomUserID, String text, long serverActionTime) {
		
		accountName=fromCustomUserID;
		content=text;
		postTime=serverActionTime;
		
		
	}
	
	
	
}
