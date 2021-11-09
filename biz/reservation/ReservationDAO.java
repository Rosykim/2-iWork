
package com.iwork.biz.reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iwork.biz.common.JDBCUtil;
import com.iwork.biz.user.UserVO;

public class ReservationDAO {
	//싱글톤 패턴사용
	private static ReservationDAO instance;
	private ReservationDAO() {}
	public static ReservationDAO getInstance() {
		if(instance == null) {
			instance = new ReservationDAO();
		}
		return instance;
	}
	
	// JDBC 관련 변수
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet rs = null;

	private final String RESERVATION_LIST = "select *from viewreservation where id=? order by pname, rdate desc"; 
	private final String RESERVATION_ALIST = "select *from viewreservation where pnum=? order by rdate desc";
	private final String RESERVATION_DELETE = "delete from reservation where rnum=?";
	private final String RESERVATION_DELETE_USER = "delete from reservation where id=?";
	
	private final String RESERVATION_ADMIN_LIST = "select p.pnum, p.pname, (select count(*) from reservation where p.pnum=pnum) as rcount FROM product p group by p.pnum, p.pname";
	
	// 예약목록 가져오기
	public List<ReservationVO> reservationList(UserVO UVO){
		List<ReservationVO> reserveList = new ArrayList<ReservationVO>();
		
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(RESERVATION_LIST);
			stmt.setString(1, UVO.getId());
			
			rs = stmt.executeQuery();
			while(rs.next()) {
				ReservationVO reserve = new ReservationVO();
				reserve.setStarttime(rs.getString("STARTTIME"));
				reserve.setEndtime(rs.getString("ENDTIME"));
				reserve.setRnum(rs.getString("RNUM"));
				reserve.setPnum(rs.getInt("PNUM"));
				reserve.setId(rs.getString("ID"));
				reserve.setRdate(rs.getDate("RDATE"));
				reserve.setPname(rs.getString("PNAME"));
				reserveList.add(reserve);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return reserveList;
	}
	
	// 관리자 예약목록 가져오기
	public List<ReservationVO> reservationAdminList(){
		List<ReservationVO> reserveList = new ArrayList<ReservationVO>();
		
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(RESERVATION_ADMIN_LIST);
			
			rs = stmt.executeQuery();
			while(rs.next()) {
				ReservationVO reserve = new ReservationVO();
				reserve.setPnum(rs.getInt("PNUM"));
				reserve.setPname(rs.getString("PNAME"));
				reserve.setRcount(rs.getInt("RCOUNT"));
				reserveList.add(reserve);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return reserveList;
	}
	
	//관리자 예약 상세
	public List<ReservationVO> reservationAlist(int pnum){
		List<ReservationVO> reserveList = new ArrayList<ReservationVO>();
		
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(RESERVATION_ALIST);
			stmt.setInt(1, pnum);
			rs = stmt.executeQuery();
			while(rs.next()) {
				ReservationVO reserve = new ReservationVO();
				reserve.setStarttime(rs.getString("STARTTIME"));
				reserve.setEndtime(rs.getString("ENDTIME"));
				reserve.setRnum(rs.getString("RNUM"));
				reserve.setPnum(rs.getInt("PNUM"));
				reserve.setId(rs.getString("ID"));
				reserve.setRdate(rs.getDate("RDATE"));
				reserve.setPname(rs.getString("PNAME"));
				reserveList.add(reserve);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return reserveList;
	}
	
	//예약목록 삭제
	public void reservationDelete(ReservationVO UVO) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(RESERVATION_DELETE);
			stmt.setString(1, UVO.getRnum());
			
			stmt.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
	//유저가 예약한 모든목록 삭제
	public void reservationDeleteUser(UserVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(RESERVATION_DELETE_USER);
			stmt.setString(1, vo.getId());
			
			stmt.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			JDBCUtil.close(stmt, conn);
		}
	}
}