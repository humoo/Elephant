package com.example.elephant.db;

import cn.bmob.v3.BmobObject;

public class LiaoTieComment extends BmobObject{

	private String parentobjectId;
	private String comment;
	private String username;
	private String significance;
	private String floorNum;
	private String zanCount;
	private String school;
	
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
	 * @return the parentobjectId
	 */
	public String getParentobjectId() {
		return parentobjectId;
	}
	/**
	 * @param parentobjectId the parentobjectId to set
	 */
	public void setParentobjectId(String parentobjectId) {
		this.parentobjectId = parentobjectId;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
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
	 * @return the floorNum
	 */
	public String getFloorNum() {
		return floorNum;
	}
	/**
	 * @param floorNum the floorNum to set
	 */
	public void setFloorNum(String floorNum) {
		this.floorNum = floorNum;
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
	
}
