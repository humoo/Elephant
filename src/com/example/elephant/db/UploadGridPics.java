package com.example.elephant.db;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class UploadGridPics extends BmobObject{

	
	private String username;
	private String picCount;
	private String bigPic;
	private String thumbPic;
	private String parentObjectId;

	/**
	 * @return the parentObjectId
	 */
	public String getParentObjectId() {
		return parentObjectId;
	}



	/**
	 * @param parentObjectId the parentObjectId to set
	 */
	public void setParentObjectId(String parentObjectId) {
		this.parentObjectId = parentObjectId;
	}



	public UploadGridPics() {
	}

	

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}



	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}



	/**
	 * @return the picCount
	 */
	public String getPicCount() {
		return picCount;
	}

	/**
	 * @param picCount the picCount to set
	 */
	public void setPicCount(String picCount) {
		this.picCount = picCount;
	}

	/**
	 * @return the bigPic
	 */
	public String getBigPic() {
		return bigPic;
	}

	/**
	 * @param bigPic the bigPic to set
	 */
	public void setBigPic(String bigPic) {
		this.bigPic = bigPic;
	}

	/**
	 * @return the thumbPic
	 */
	public String getThumbPic() {
		return thumbPic;
	}

	/**
	 * @param string the thumbPic to set
	 */
	public void setThumbPic(String string) {
		this.thumbPic = string;
	}
	
	
}
