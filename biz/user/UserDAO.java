package com.iwork.biz.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.iwork.biz.common.JDBCUtil;


public class UserDAO {
	// JDBC 관련 변수
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet rs = null;
	
	// SQL 명령어들
	/**로그인*/
	private final String USER_GET = "select * from USERS where ID=? and PASSWORD=?";
	/**회원가입*/
	private final String JOIN_GET = "insert into USERS values (?,?,?,?,?,'user')";
	/**탈퇴 */
	private final String DELETE_USER ="delete from USERS where ID =?";
	/**회원정보 수정-해당 아이디만 수정 가능하도록 */
	private final String EDIT_USER ="update USERS set NAME=?,tel=?,EMAIL=?, role=? where ID=?";
	/**회원전체 검색하기*/
	private final String USER_GETALL ="select u.*,(select count(*) from reservation where id=u.id) rcount  from USERS u";
	/**회원 한명 검색 기능*/
	private final String USER_GETONE ="select * from USERS where ID =?";
	/**아이디 중복확인*/
	private final String USERID_CHECK ="select count(*) id from USERS where ID=? ";
	/**아이디 찾기*/
	private final String FIND_ID = "select id from users where name=? and email=?";
	/**비밀번호 찾기*/
	private final String FIND_PW = "select password from users where id=? and name=? and email=?";
	/**비밀번호 변경*/
	private final String CHANGE_PW = "update USERS set PASSWORD=? where ID=?";

	// CRUD 기능의 메소드 구현
	// 회원등록
	/** 데터베이스 쿼리문*/
	
	/** 로그인*/
	public UserVO login(UserVO vo) {
		
//		수정-------------------------
//		UserVO user = null;
		UserVO user = new UserVO();
//		-----------------------------
		try {
			conn =JDBCUtil.getConnection();
			stmt = conn.prepareStatement(USER_GET);
			stmt.setString(1, vo.getId());
			stmt.setString(2, vo.getPassword());
			rs = stmt.executeQuery();
			
			while(rs.next()) {
//				삭제------------------
//				user = new UserVO();
//				---------------------
				user.setId(rs.getString("ID"));
				user.setPassword(rs.getString("PASSWORD"));
				user.setName(rs.getString("NAME"));
				user.setTel(rs.getString("TEL"));
				user.setEmail(rs.getString("EMAIL"));
				user.setRole(rs.getString("ROLE"));	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return user;
	}
	
	/**회원가입*/
	public UserVO getJoin(UserVO vo) {
		UserVO user = null;
		try {
			conn =JDBCUtil.getConnection();
			stmt = conn.prepareStatement(JOIN_GET);
			stmt.setString(1, vo.getId());
			stmt.setString(2, vo.getPassword());
			stmt.setString(3, vo.getName());
			stmt.setString(4, vo.getTel());
			stmt.setString(5, vo.getEmail());
			
			stmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
		return user;
	
	}
	/**회원전체 검색*/
	public List<UserVO> userGetall() {
		List<UserVO> userList = new ArrayList<UserVO>();
		try {
			
			conn =JDBCUtil.getConnection();
			stmt = conn.prepareStatement(USER_GETALL);
			rs = stmt.executeQuery();
			while(rs.next()) {
				UserVO user = new UserVO();
				
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setTel(rs.getString("tel"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setRcount(rs.getInt("rcount"));
				
				userList.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return userList;
	}
	/**회원 탈퇴*/

	public void deleteUser(UserVO vo) {
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(DELETE_USER);
			stmt.setString(1, vo.getId());

			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
	
	/** 회원수정 */
	public void editUser(UserVO vo) {
//			update USERS set PASSWORD=?,NAME=?,tel=?,EMAIL=?,where ID=?
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(EDIT_USER);
			stmt.setString(1, vo.getName());
			stmt.setString(2, vo.getTel());
			stmt.setString(3, vo.getEmail());
			stmt.setString(4, vo.getRole());
			stmt.setString(5, vo.getId());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
	}
		
	/**한명 검색 */
	public UserVO userGetone(UserVO vo) {
		UserVO user = new UserVO();
		
		try {
			conn =JDBCUtil.getConnection();
			stmt = conn.prepareStatement(USER_GETONE);
			stmt.setString(1, vo.getId());
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setTel(rs.getString("tel"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs, stmt, conn);
		}
		return user;
	}
	
	/**아이디 중복확인*/
	public int userIdCheck(UserVO vo) {
		int result = 0;
			try {
			conn=JDBCUtil.getConnection();
			stmt = conn.prepareStatement(USERID_CHECK);
			stmt.setString(1, vo.getId());
			
			rs = stmt.executeQuery();
			if(rs.next()) {
				result = rs.getInt("id");		
			}
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt,conn);
		}
		return result;
	}
	
	/**아이디찾기*/
	public List<String> findId(UserVO vo) {
		List<String> idList = new ArrayList<String>();
		try {
			conn=JDBCUtil.getConnection();
			stmt = conn.prepareStatement(FIND_ID);
			stmt.setString(1, vo.getName());
			stmt.setString(2, vo.getEmail());
			
			rs = stmt.executeQuery();
			while(rs.next()) {
				String id = rs.getString("id");	
				idList.add(id);	
			}
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt,conn);
		}
		return idList;
	}
	
	/**비밀번호 찾기*/
	public String findPw(UserVO vo) {
		String pw = "";
		try {
			conn=JDBCUtil.getConnection();
			stmt = conn.prepareStatement(FIND_PW);
			stmt.setNString(1, vo.getId());
			stmt.setString(2, vo.getName());
			stmt.setString(3, vo.getEmail());
			
			rs = stmt.executeQuery();
			while(rs.next()) {
				pw = rs.getString("password");	
			}
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt,conn);
		}
		return pw;
	}
	
	/** 비밀번호 수정 */
	public String changePw(UserVO vo) {
		String flag = "0";
		try {
			conn = JDBCUtil.getConnection();
			stmt = conn.prepareStatement(CHANGE_PW);
			stmt.setString(1, vo.getPassword());
			stmt.setString(2, vo.getId());
			stmt.executeUpdate();
			flag = "1";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(stmt, conn);
		}
		return flag;
	}
	
}