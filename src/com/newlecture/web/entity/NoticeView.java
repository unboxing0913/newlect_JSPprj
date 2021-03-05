package com.newlecture.web.entity;

import java.util.Date;

public class NoticeView extends Notice {

	private int cmtCount;
	
	
	public int getCmtCount() {
		return cmtCount;
	}

	public void setCmtCount(int cmtCount) {
		this.cmtCount = cmtCount;
	}
	
	public NoticeView() {
		// TODO Auto-generated constructor stub
	}
	

	public NoticeView(int id, String title, Date regdate, String writerId, int hit, String files, int cmtCount, boolean pub) {
		super(id, title, regdate, writerId, hit, files, "",pub);
		this.cmtCount = cmtCount;
	}

}
