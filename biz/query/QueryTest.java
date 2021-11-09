package com.iwork.biz.query;


public class QueryTest {
	public static void main(String[] args) {
		
		
		QueryDAO dao = QueryDAO.getInstance();
		
		for(int i = 0; i <= 50; i++) {
			QueryVO vo = new QueryVO();
			vo.setQuestion("페이징" + i);
			vo.setAnswer("페이징 테스트" + i);
			
			dao.insertQuery(vo);
			System.out.println();
		}
	}
}
