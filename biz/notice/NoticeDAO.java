/*작성자 이윤석*/

package com.iwork.biz.notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iwork.biz.common.JDBCUtil;

public class NoticeDAO {
	//싱글톤 패턴사용
	private static NoticeDAO instance;
	private NoticeDAO() {}
	public static NoticeDAO getInstance() {
		if(instance == null) {
			instance = new NoticeDAO();
		}
		return instance;
	}
	
	// JDBC 관련 변수
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet rs = null;
	
	private final String getNoticeList = "select * from notice order by nseq desc";
	private final String getNotice = "select * from notice where nseq=?";
	private final String insertNotice = "insert into notice values((select nvl(max(nseq), 0)+1 from notice), ?, ?, 0, sysdate)";
	private final String updateNotice = "update notice set title=?, content=? where nseq=?";
	private final String deleteNotice = "delete from notice where nseq =?";
	private final String maxNseq = "select max(nseq) nseq from notice";
	
	
	private final String NOTICE_COUNT = "select count(nseq) as nseq from notice";
	private final String NOTICE_LIST_CNT = "SELECT * FROM(SELECT A.*, ROWNUM AS RNUM, COUNT(*)" + 
			"OVER() AS TOTCNT FROM(SELECT * FROM notice ORDER BY nseq desc) A)" + 
			"WHERE RNUM > ? AND RNUM <= ? order by nseq desc";
	
	
//	공지목록
	public List<NoticeVO> getNoticeList() {
		List<NoticeVO> noticeList = new ArrayList<NoticeVO>();
		
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(getNoticeList);
			rs = stmt.executeQuery();
			while(rs.next()) {
				NoticeVO notice = new NoticeVO();
				notice.setNseq(rs.getInt("nseq"));
				notice.setTitle(rs.getString("title"));
				notice.setContent(rs.getString("content"));
				notice.setCnt(rs.getInt("cnt"));
				notice.setRegdate(rs.getDate("regdate"));
				noticeList.add(notice);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return noticeList;
	}
	
//	공지 상세
	public NoticeVO getNotice(NoticeVO vo) {
		NoticeVO notice = new NoticeVO();
		
		try {
			
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(getNotice);
			stmt.setInt(1, vo.getNseq());
			rs = stmt.executeQuery();
			while(rs.next()) {
				notice.setNseq(rs.getInt("nseq"));
				notice.setTitle(rs.getString("title"));
				notice.setContent(rs.getString("content"));
				notice.setCnt(rs.getInt("cnt"));
				notice.setRegdate(rs.getDate("regdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return notice;
	}
	
//	공지 등록
	public void insertNotice(NoticeVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(insertNotice);
			stmt.setString(1, vo.getTitle());
			stmt.setString(2, vo.getContent());
			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
//	공지 수정
	public void updateNotice(NoticeVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(updateNotice);
			stmt.setString(1, vo.getTitle());
			stmt.setString(2, vo.getContent());
			stmt.setInt(3, vo.getNseq());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
//	공지 삭제
	public void deleteNotice(NoticeVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(deleteNotice);
			stmt.setInt(1, vo.getNseq());
			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
//	공지글을 등록하고 작성한 글 상세보기로 이동을 할때 글번호를 가져오기위해 사용
	public int getMaxNseq() {
		NoticeVO vo = new NoticeVO();
		try {
			
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(maxNseq);
			rs = stmt.executeQuery();
			if(rs.next()) {
				vo.setNseq(rs.getInt("nseq"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return vo.getNseq();
	}
	
	/*------------------------페이징 관련---------------------------*/
	
//	전체 게시물 개수
	public int noticeCount() {
		int nseq = 0;
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(NOTICE_COUNT);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				nseq = rs.getInt("nseq");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
		return nseq;
	}
	
//	공지목록 + 페이징처리
	public List<NoticeVO> getNoticeListCnt(NoticeVO vo) {
		List<NoticeVO> noticeList = new ArrayList<NoticeVO>();
		Pagination pagination = new Pagination();
		
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(NOTICE_LIST_CNT);
			stmt.setInt(1, vo.getStartIndex());
			stmt.setInt(2, vo.getStartIndex() + pagination.getPageSize());
			
			rs = stmt.executeQuery();
			while(rs.next()) {
				NoticeVO notice = new NoticeVO();
				notice.setNseq(rs.getInt("nseq"));
				notice.setTitle(rs.getString("title"));
				notice.setContent(rs.getString("content"));
				notice.setCnt(rs.getInt("cnt"));
				notice.setRegdate(rs.getDate("regdate"));
				noticeList.add(notice);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return noticeList;
	}
	
	
}
