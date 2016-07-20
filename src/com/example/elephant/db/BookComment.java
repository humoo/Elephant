package com.example.elephant.db;

import cn.bmob.v3.BmobObject;

public class BookComment extends BmobObject {

	private String parentobjectId;
	private String comment;
	private String username;
	private String significance;
	private String floorNum;
	private String school;
	
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}

	public void setFloorNum(String floorNum) {
		this.floorNum = floorNum;
	}
	public String getFloorNum() {
		return floorNum;
	}
	public void setParentobjectId(String parentobjectId) {
		this.parentobjectId = parentobjectId;
	}
	public String getParentobjectId() {
		return parentobjectId;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getComment() {
		return comment;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setSignificance(String significance) {
		this.significance = significance;
	}
	public String getSignificance() {
		return significance;
	}
	
}
