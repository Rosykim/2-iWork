/*작성자 이윤석*/

package com.iwork.biz.notice;

import java.sql.Date;

public class NoticeVO {

	private int nseq;
	private String title;
	private String content;
	private int cnt;
	private Date regdate;
	private String[] contentList;
	
	public String[] getContentList() {
		return contentList;
	}
	public void setContentList(String[] contentList) {
		this.contentList = contentList;
	}
	//	페이징 관련
	private int startIndex;
	private int cntPerPage;
	
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getCntPerPage() {
		return cntPerPage;
	}
	public void setCntPerPage(int cntPerPage) {
		this.cntPerPage = cntPerPage;
	}
	public int getNseq() {
		return nseq;
	}
	public void setNseq(int nseq) {
		this.nseq = nseq;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public Date getRegdate() {
		return regdate;
	}
	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}
	
}
