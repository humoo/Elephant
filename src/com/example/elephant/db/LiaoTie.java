package com.example.elephant.db;

import cn.bmob.v3.BmobObject;

public class LiaoTie extends BmobObject{

	String username;
	String usercontent;
	String significance;
	String commentCount;
	String zanCount;
	String school;
	String zanUsers;
	String pics;
	String bigPicPathString;
	String thumbPicPathString;

	/**
	 * @return the bigPicPathString
	 */
	public String getBigPicPathString() {
		return bigPicPathString;
	}
	/**
	 * @param bigPicPathString the bigPicPathString to set
	 */
	public void setBigPicPathString(String bigPicPathString) {
		this.bigPicPathString = bigPicPathString;
	}
	/**
	 * @return the thumbPicPathString
	 */
	public String getThumbPicPathString() {
		return thumbPicPathString;
	}
	/**
	 * @param thumbPicPathString the thumbPicPathString to set
	 */
	public void setThumbPicPathString(String thumbPicPathString) {
		this.thumbPicPathString = thumbPicPathString;
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
	 * @return the usercontent
	 */
	public String getUsercontent() {
		return usercontent;
	}
	/**
	 * @param usercontent the usercontent to set
	 */
	public void setUsercontent(String usercontent) {
		this.usercontent = usercontent;
	}
	/**
	 * @return the significance
	 */
	public String getSignificance() {
		return significance;
	}
	/**
	 * @param significance the significance to set
	 */
	public void setSignificance(String significance) {
		this.significance = significance;
	}
	/**
	 * @return the commentCount
	 */
	public String getCommentCount() {
		return commentCount;
	}
	/**
	 * @param commentCount the commentCount to set
	 */
	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
	/**
	 * @return the zanCount
	 */
	public String getZanCount() {
		return zanCount;
	}
	/**
	 * @param zanCount the zanCount to set
	 */
	public void setZanCount(String zanCount) {
		this.zanCount = zanCount;
	}
	/**
	 * @return the school
	 */
	public String getSchool() {
		return school;
	}
	/**
	 * @param school the school to set
	 */
	public void setSchool(String school) {
		this.school = school;
	}
	/**
	 * @return the zanUsers
	 */
	public String getZanUsers() {
		return zanUsers;
	}
	/**
	 * @param zanUsers the zanUsers to set
	 */
	public void setZanUsers(String zanUsers) {
		this.zanUsers = zanUsers;
	}
	/**
	 * @return the pics
	 */
	public String getPics() {
		return pics;
	}
	/**
	 * @param pics the pics to set
	 */
	public void setPics(String pics) {
		this.pics = pics;
	}
	
	
}
