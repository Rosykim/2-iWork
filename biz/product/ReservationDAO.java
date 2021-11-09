/*작성 이윤석*/

package com.iwork.biz.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iwork.biz.common.JDBCUtil;

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

	// 스터디룸의 날짜에 예약되있는 시작시간 종료시간 가져옴
	private final String RESERVATION_INFO = "select STARTTIME, ENDTIME from reservation where pnum=? and rdate=?";
	
	private final String RESERVATION_ALL_DELETE = "DELETE FROM RESERVATION where pnum = ?";
	
	/**예약 시간정보 반환*/
	public List<ReservationVO> reservationTimeList(ReservationVO vo) {
		//날짜와 방번호를 받아서 해당하는 방의 해당 날짜에 예약되있는 시간을 리스트로 반환 
		List<ReservationVO> reserveList = new ArrayList<ReservationVO>();
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(RESERVATION_INFO);
			stmt.setInt(1, vo.getPnum());
			stmt.setDate(2, vo.getRdate());
			
			rs = stmt.executeQuery();
			while(rs.next()) {
				ReservationVO reserve = new ReservationVO();
				reserve.setStarttime(rs.getString("STARTTIME"));
				reserve.setEndtime(rs.getString("ENDTIME"));
				reserveList.add(reserve);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return reserveList;
	}
	
	//0~23까지 총 24칸의 배열을 만들어서 24시~01시 예약이면 0번째 배열을 1로 01시~02시 예약이면 1번째 배열을 1로 바꿈
	public int[] reservationTimeArr(List<ReservationVO> reserveList) {
		//24칸짜리 배열 생성
		int[] rt = new int[24];
		
		//모든칸을 0으로 초기화
		for(int i = 0; i < rt.length; i++) {
			rt[i] = 0;
		}
		
		//db에서 가져온 예약정보 개수만큼 반복문을 돌림
		for(int i = 0; i < reserveList.size(); i++) {
			//n번째 정보를 reserve에 입력
			ReservationVO reserve = reserveList.get(i);
			
			//String 으로 되있는시간을 계산하기위해 int 형으로 변환
			int start = Integer.parseInt(reserve.getStarttime());
			int end = Integer.parseInt(reserve.getEndtime());
			
			//24시를 0시로변경
			if(start == 24) {
				start = 0;
			}
			
			/*만약13시~15시까지 2시간 예약을 했을경우
			 * 15-13 = 2회반복실행
			 * n번째 값을 1로 변경
			 * 예약정보 개수만큼 반복실행
			 */
			for(int j = start; j < end; j++) {
				rt[j] = 1;
			}
		}
		
		return rt;
	}
	
	/** 해당 pnum의 예약목록 전체삭제*/
	public void deleteReservation(ReservationVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(RESERVATION_ALL_DELETE);
			stmt.setInt(1, vo.getPnum());
			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
}
