package com.example.elephant.im.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShowChatMsgListView {

	private static String accountName;
	private static String content;
	private static long postTime;
	public static boolean isInit=false;//�����Ƿ��Ѿ���ʼ����
	
	public ShowChatMsgListView() {
		
		isInit=true;
		
		
		
	}
	
	/**
	 * �����б���Ϣ������Ϣ���浽���ݿ�
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
