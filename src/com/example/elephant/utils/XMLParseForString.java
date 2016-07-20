package com.example.elephant.utils;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XMLParseForString {
	
	/**
	 * 对输入流信息进行解析
	 * @param is
	 */
	String version;
	String description;
	String apkurl;
	/**
	 * 解析输入流携带的xml信息
	 * @param is
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public XMLParseForString(InputStream is) throws XmlPullParserException, IOException {

		//Returns a new pull parser with namespace support.
		XmlPullParser parser=Xml.newPullParser();

		//将要解析的文件流传入
		parser.setInput(is,"utf-8");
		//Returns the type of the current event (START_TAG, END_TAG, TEXT, etc.)
		int type=parser.getEventType();

		while (type!=XmlPullParser.END_DOCUMENT) {
			if (type==XmlPullParser.START_TAG) {
				if ("version".equals(parser.getName())) {
					setVersion(parser.nextText());//开始标签后就是文本
				}else if ("description".equals(parser.getName())) {

					setDescription(parser.nextText());;
				}else if ("apkurl".equals(parser.getName())) {

					setApkurl(parser.nextText());
				}
				
			}
			
			type=parser.next();//下一个标签
		}
		
	}
	
	public void setApkurl(String apkurl) {
		this.apkurl = apkurl;
	}
	public String getApkurl() {
		return apkurl;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersion() {
		return version;
	}	
	
}
