package com.iwork.biz.query;

public class QueryVO {

	private int qseq;
	private String question;
	private String answer;
	private String[] contentList;
	
// 페이징 관련
	private int startIndex;
	private int cntPerPage;
	
	public String[] getContentList() {
		return contentList;
	}
	public void setContentList(String[] contentList) {
		this.contentList = contentList;
	}
	public int getQseq() {
		return qseq;
	}
	public void setQseq(int qseq) {
		this.qseq = qseq;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
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
	
	
	
	
}
