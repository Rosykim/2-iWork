package com.iwork.biz.notice;

public class noticeTest {
	
	public static void main(String[] args) {
		NoticeDAO dao = NoticeDAO.getInstance();
			
		for(int i = 0; i <= 50; i++) {
			NoticeVO vo = new NoticeVO();
			vo.setTitle("페이징" + i);
			vo.setContent("페이징 테스트" + i);
			
			dao.insertNotice(vo);
			System.out.println();
		}
	}
}
