package com.example.elephant.db;

import cn.bmob.v3.BmobObject;

public class MainContent extends BmobObject {

	// 用户信息
	private String username;
	private String usercontent;
	private String significance;
	private String school;

	// 书籍信息
	private String bookname;
	private String bookisbn;
	private String bookauthor;
	private String bookpublisher;

	// 用户流量
	private String viewcount;
	private String commentcount;

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public void setCommentcount(String commentcount) {
		this.commentcount = commentcount;
	}

	public String getCommentcount() {
		return commentcount;
	}

	public void setViewcount(String viewcount) {
		this.viewcount = viewcount;
	}

	public String getViewcount() {
		return viewcount;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsercontent(String usercontent) {
		this.usercontent = usercontent;
	}

	public String getUsercontent() {
		return usercontent;
	}

	public void setSignificance(String significance) {
		this.significance = significance;
	}

	public String getSignificance() {
		return significance;
	}

	public void setBookauthor(String bookauthor) {
		this.bookauthor = bookauthor;
	}

	public String getBookauthor() {
		return bookauthor;
	}

	public void setBookisbn(String bookisbn) {
		this.bookisbn = bookisbn;
	}

	public String getBookisbn() {
		return bookisbn;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	public String getBookname() {
		return bookname;
	}

	public void setBookpublisher(String bookpublisher) {
		this.bookpublisher = bookpublisher;
	}

	public String getBookpublisher() {
		return bookpublisher;
	}

}
