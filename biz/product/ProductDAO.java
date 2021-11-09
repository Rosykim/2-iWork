
package com.iwork.biz.product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.iwork.biz.common.JDBCUtil;
import com.iwork.biz.reservation.ReservationVO;
import com.iwork.biz.user.UserVO;

@Repository
public class ProductDAO {
	//싱글톤 패턴사용
	private static ProductDAO instance;
	private ProductDAO() {}
	public static ProductDAO getInstance() {
		if(instance == null) {
			instance = new ProductDAO();
		}
		return instance;
	}
	
	// JDBC 관련 변수
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet rs = null;
		
	//데이터베이스 쿼리문
	/**상품리스트 전체 가져오기*/
	private final String PRODUCT_LIST = "select * from product order by pnum";
	/**상품 가져오기*/
	private final String PRODUCT_SELET = "select * from product where pnum=?";
	/**상품 추가*/
	private final String PRODUCT_INSERT = " insert into product values((select nvl(max(pnum), 0)+1 from product), ?, ?, ?, ?)";
	/**상품 삭제*/
	private final String PRODUCT_DELETE = "delete product where pnum=?";
	/**상품 업데이트*/
	private final String PRODUCT_UPDATE = "update product set pname=?, price=?, imgname=?, maxpeople=? where pnum=?";
	/**사진 업데이트*/
	private final String PRODUCT_UPDATE_IMG = "update product set imgname=? where pnum=?";
	/**상품 예약*/													//예약번호, 방번호, 예약자 아이디, 예약일, 시작시간, 끝시간
	private final String PRODUCT_RESERVATION = "insert into reservation values(?, ?, ?, ?, ?, ?)";
	/**방번호로 방이름 검색*/
	private final String PRODUCT_NAME_SEARCH_FOR_NUM = "select pname from product where pnum = ?";
	/**파일 이름 검색*/
	private final String GET_FILENAME = "select imgname from product where pnum = ?";
	
	/**상품리스트 전체 반환*/
	public List<ProductVO> getProductList() {
		List<ProductVO> productList = new ArrayList<ProductVO>();
		
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(PRODUCT_LIST);
			rs = stmt.executeQuery();
			while(rs.next()) {
				ProductVO product = new ProductVO();
				product.setPnum(rs.getInt("pnum"));
				product.setPname(rs.getString("pname"));
				product.setPrice(rs.getInt("price"));
				product.setMaxpeople(rs.getInt("maxpeople"));
				product.setImgname(rs.getString("imgname"));
				productList.add(product);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return productList;
	}
	
	/**상품 상세정보 반환*/
	public ProductVO getProduct(ProductVO vo) {
		ProductVO product = new ProductVO();
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(PRODUCT_SELET);
			stmt.setInt(1, vo.getPnum());
			rs = stmt.executeQuery();
			while(rs.next()) {
				product.setPnum(rs.getInt("pnum"));
				product.setPname(rs.getString("pname"));
				product.setPrice(rs.getInt("price"));
				product.setMaxpeople(rs.getInt("maxpeople"));
				product.setImgname(rs.getString("imgname"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return product;
	}
	
	/**상픔 등록*/
	public void insertProduct(ProductVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			if(vo.getImgname() == null || vo.getImgname().equals("")) {
				vo.setImgname("");
			}
			stmt = conn.prepareStatement(PRODUCT_INSERT);
			//상품이름 가격 최대인원 파일이름
			stmt.setString(1, vo.getPname());
			stmt.setInt(2, vo.getPrice());
			stmt.setInt(3, vo.getMaxpeople());
			stmt.setString(4, vo.getImgname());
			
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
	/**상품 삭제*/
	public void productDelete(ProductVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(PRODUCT_DELETE);
			stmt.setInt(1, vo.getPnum());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
	/**상품 수정*/
	public void productUpdate(ProductVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(PRODUCT_UPDATE);
			stmt.setString(1, vo.getPname());
			stmt.setInt(2, vo.getPrice());
			stmt.setString(3, vo.getImgname());
			stmt.setInt(4, vo.getMaxpeople());
			stmt.setInt(5, vo.getPnum());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
	//현제 시간 가져오기 상품예약번호 만들때 필요
	private String nowTime() {
		String nowtime = "";
		
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement("SELECT TO_CHAR(SYSDATE, 'MMDDHHMISS') as nowtime FROM DUAL");
			rs = stmt.executeQuery();
			while(rs.next()) {
				nowtime = rs.getString("nowtime");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		
		return nowtime;
	}
	
	/**상품 번호로 상품 이름검색*/
	public ProductVO getProductName(ProductVO vo) {
		ProductVO product = new ProductVO();
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(PRODUCT_NAME_SEARCH_FOR_NUM);
			stmt.setInt(1, vo.getPnum());
			rs = stmt.executeQuery();
			while(rs.next()) {
				product.setPname(rs.getString("pname"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return product;
	}
	
	/**상픔 예약*/
	public boolean reservationProduct(UserVO uvo, ProductVO pvo, ReservationVO rvo) {
		boolean flag = false;
		String rnum = nowTime();
		rnum += uvo.getTel();
		if(!checkReservation(rvo)) {
			return false;
		}
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(PRODUCT_RESERVATION);
			//예약번호, 방번호, 예약자 아이디, 예약일, 시작시간, 끝시간
			stmt.setString(1, rnum);
			stmt.setInt(2, rvo.getPnum());
			stmt.setString(3, uvo.getId());
			stmt.setDate(4, rvo.getRdate());
			stmt.setString(5, rvo.getStarttime());
			stmt.setString(6, rvo.getEndtime());
			
			stmt.executeUpdate();
			flag = true;
//			System.out.println("예약번호 : " + rnum);
//			System.out.println("예약자 : " + uvo.getId() );
//			System.out.println("방번호 : " + rvo.getPnum());
//			System.out.println("예약일 : " + rvo.getRdate());
//			System.out.println("시작시간 : " + rvo.getStarttime());
//			System.out.println("종료시간 : " + rvo.getEndtime());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
		return flag;
	}
	
	//상품 수정시 사진을 다른사진으로 바꿀때 기존 사진을 삭제하기위해 사용
	/**파일 이름 반환*/
	public ProductVO getFileName(ProductVO vo) {
		ProductVO product = new ProductVO();
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(GET_FILENAME);
			stmt.setInt(1, vo.getPnum());
			rs = stmt.executeQuery();
			while(rs.next()) {
				product.setImgname(rs.getString("imgname"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return product;
	}
	
	public void productUpdateImg(ProductVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(PRODUCT_UPDATE_IMG);
			stmt.setString(1, vo.getImgname());
			stmt.setInt(2, vo.getPnum());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
	/*----------------------예약 관련--------------------------------*/
//	스터디룸의 날짜에 예약되있는 시작시간 종료시간 가져옴
	private final String RESERVATION_INFO = "select STARTTIME, ENDTIME from reservation where pnum=? and rdate=?";
//	스터디룸을 삭제하려면 무결성 제약조건 때문에 해당스터디룸의 예약정보를 삭제해야함
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
	
//	중복예약방지
	public boolean checkReservation(ReservationVO vo) {
		List<ReservationVO> reservationList = new ArrayList<ReservationVO>();
		boolean flag = false;
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(RESERVATION_INFO);
			stmt.setInt(1, vo.getPnum());
			stmt.setDate(2, vo.getRdate());
			rs = stmt.executeQuery();
			if(rs.next()) {
				do {
					ReservationVO reservation = new ReservationVO();
					reservation.setStarttime(rs.getString("starttime"));
					reservation.setEndtime(rs.getString("endtime"));
					reservationList.add(reservation);
				}while(rs.next());
			} else {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		
		for(ReservationVO r : reservationList) {
			if(r.getStarttime().equals(vo.getStarttime())) {
				return false;
			}
			if(Integer.parseInt(r.getStarttime()) < Integer.parseInt(vo.getStarttime())) {
				if(Integer.parseInt(r.getEndtime()) <= Integer.parseInt(vo.getStarttime())){
					flag = true;
				}
			} else if(Integer.parseInt(r.getStarttime()) > Integer.parseInt(vo.getStarttime())) {
				if(Integer.parseInt(vo.getEndtime()) <= Integer.parseInt(r.getStarttime())) {
					flag = true;
				}
			}
		}
		
		return flag;
	}
		
}
