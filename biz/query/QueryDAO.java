package com.iwork.biz.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iwork.biz.common.JDBCUtil;

public class QueryDAO {
	//싱글톤 패턴사용
	private static QueryDAO instance;
	private QueryDAO() {}
	public static QueryDAO getInstance() {
		if(instance == null) {
			instance = new QueryDAO();
		}
		return instance;
	}
	
	// JDBC 관련 변수
		private Connection conn = null;
		private PreparedStatement stmt = null;
		private ResultSet rs = null;
		
		private final String getQueryList = "select * from Query order by qseq desc";
		private final String getQuery = "select * from Query where qseq=?";
		private final String insertQuery = "insert into Query values((select nvl(max(qseq), 0)+1 from Query), ?, ?)";
		private final String updateQuery = "update Query set question=?, answer=? where qseq=?";
		private final String deleteQuery = "delete from Query where qseq =?";
		private final String maxQseq = "select max(qseq) qseq from Query";
		
		
		private final String QUERY_COUNT = "select count(qseq) as qseq from Query";
		private final String QUERY_LIST_CNT = "SELECT * FROM(SELECT A.*, ROWNUM AS RNUM, COUNT(*)" + 
				"OVER() AS TOTCNT FROM(SELECT * FROM Query ORDER BY qseq desc) A)" + 
				"WHERE RNUM > ? AND RNUM <= ? order by qseq desc";
		
	
//Q&A목록
		public List<QueryVO> getQueryList() {
			List<QueryVO> queryList = new ArrayList<QueryVO>();
			
			try {
				conn = JDBCUtil.getConnection();
				stmt = conn.prepareStatement(getQueryList);
				rs = stmt.executeQuery();
				while(rs.next()) {
					QueryVO query = new QueryVO();
					query.setQseq(rs.getInt("qseq"));
					query.setQuestion(rs.getString("question"));
					queryList.add(query);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs, stmt, conn);
			}
			return queryList;
		}
		
		
//	Q&A	 상세
		public QueryVO getQuery(QueryVO vo) {
			QueryVO query = new QueryVO();
			
			try {
				
				conn = JDBCUtil.getConnection();
				stmt = conn.prepareStatement(getQuery);
				stmt.setInt(1, vo.getQseq());
				rs = stmt.executeQuery();
				while(rs.next()) {
					query.setQseq(rs.getInt("qseq"));
					query.setQuestion(rs.getString("question"));
					query.setAnswer(rs.getString("answer"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs, stmt, conn);
			}
			return query;
		}
		
		
//Q&A 등록
		public void insertQuery(QueryVO vo) {
			try {
				conn = JDBCUtil.getConnection();
				stmt = conn.prepareStatement(insertQuery);
				stmt.setString(1, vo.getQuestion());
				stmt.setString(2, vo.getAnswer());
				stmt.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(stmt, conn);
			}
		}
		
		
//		공지 수정
		public void updateQuery(QueryVO vo) {
			try {
				conn = JDBCUtil.getConnection();
				stmt = conn.prepareStatement(updateQuery);
				stmt.setString(1, vo.getQuestion());
				stmt.setString(2, vo.getAnswer());
				stmt.setInt(3, vo.getQseq());
				stmt.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(stmt, conn);
			}
		}
		
//		Q&A 삭제
		public void deleteQuery(QueryVO vo) {
			try {
				conn = JDBCUtil.getConnection();
				stmt = conn.prepareStatement(deleteQuery);
				stmt.setInt(1, vo.getQseq());
				stmt.executeUpdate();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(stmt, conn);
			}
		}
		
//		Q&A글을 등록하고 작성한 글 상세보기로 이동을 할때 글번호를 가져오기위해 사용
		public int getMaxQseq() {
			QueryVO vo = new QueryVO();
			try {
				
				conn = JDBCUtil.getConnection();
				stmt = conn.prepareStatement(maxQseq);
				rs = stmt.executeQuery();
				if(rs.next()) {
					vo.setQseq(rs.getInt("qseq"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs,stmt, conn);
			}
			return vo.getQseq();
		}
		
		/*------------------------페이징 관련---------------------------*/
		
//		전체 게시물 개수
		public int queryCount() {
			int qseq = 0;
			try {
				conn = JDBCUtil.getConnection();
				stmt = conn.prepareStatement(QUERY_COUNT);
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					qseq = rs.getInt("qseq");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs,stmt, conn);
			}
			return qseq;
		}
		
//		공지목록 + 페이징처리
		public List<QueryVO> getQueryListCnt(QueryVO vo) {
			List<QueryVO> queryList = new ArrayList<QueryVO>();
			Pagination pagination = new Pagination();
			
			try {
				conn = JDBCUtil.getConnection();
				stmt = conn.prepareStatement(QUERY_LIST_CNT);
				stmt.setInt(1, vo.getStartIndex());
				stmt.setInt(2, vo.getStartIndex() + pagination.getPageSize());
				
				rs = stmt.executeQuery();
				while(rs.next()) {
					QueryVO query = new QueryVO();
					query.setQseq(rs.getInt("qseq"));
					query.setQuestion(rs.getString("question"));
					query.setAnswer(rs.getString("answer"));
					queryList.add(query);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.close(rs, stmt, conn);
			}
			return queryList;
		}
		
		
		
		
		
}
