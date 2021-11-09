package com.iwork.view.controller;



import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwork.biz.notice.NoticeDAO;
import com.iwork.biz.notice.NoticeVO;
import com.iwork.biz.query.QueryDAO;
import com.iwork.biz.query.QueryVO;
import com.iwork.biz.reservation.ReservationDAO;
import com.iwork.biz.user.UserDAO;
import com.iwork.biz.user.UserVO;
/**원하는 기능 불러올수 있는 메소드 */
@Controller
public class UserController {
	
	UserDAO dao = new UserDAO();
	NoticeDAO ndao = NoticeDAO.getInstance();
	QueryDAO qdao = QueryDAO.getInstance();
	
//	메인화면
	@RequestMapping("/")
	public String mainpage(HttpServletRequest request, Model model, UserVO vo) {

		List<NoticeVO> noticeList = new ArrayList<NoticeVO>();
		List<QueryVO> queryList = new ArrayList<QueryVO>();
		
		List<NoticeVO> nList = ndao.getNoticeList();
		List<QueryVO> qList = qdao.getQueryList();
		
		if(nList.size() > 5) {
	         for(int i = 0; i < 5; i++) {
	            noticeList.add(nList.get(i));
	         }
	      } else {
	         for(int i = 0; i < nList.size(); i++) {
	            noticeList.add(nList.get(i));
	         }
	      }
	      if(qList.size() > 5) {
	         for(int i = 0; i < 5; i++) {
	            queryList.add(qList.get(i));
	         }
	      } else {
	         for(int i = 0; i < qList.size(); i++) {
	            queryList.add(qList.get(i));
	         }
	      }
		
		request.getSession().setAttribute("page", 1);
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("queryList", queryList);
		
		return "/index";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginGet() {
		return "/user/login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost(UserVO vo, Model model, HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		UserVO user = dao.login(vo);
		if(user.getRole() != null) {
			session.setAttribute("id", user.getId());
			session.setAttribute("role", user.getRole());
			return "redirect:/";
		} else {
			return "redirect:/login?err=1";
		}
		
	}
	
	@RequestMapping(value="/join", method = RequestMethod.GET)
	public String join() {
		return "/user/join";
	}
	
	//session에 아이디 정보 저장하고 메인.jsp로 돌아가는 기능 추가 
	@RequestMapping(value="/join", method = RequestMethod.POST)
	public String joinPro(UserVO vo, Model model) {
		dao.getJoin(vo);
		
		return "redirect:/";
	}
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		
		return "redirect:/login";
	}
	
	@RequestMapping(value = "/idcheck", method = RequestMethod.POST)
	@ResponseBody
	public int userCheck(String id) {
		UserVO vo = new UserVO();
		vo.setId(id);
		int idCheck = dao.userIdCheck(vo);
		
		return idCheck;
	}
	
	/*    비밀번호 인증시스템
	 * 1. a라는 링크 접근시 비밀번호 인증이 필요하다면
	 * 2. model.addAttribute에 nextpage라는 이름으로 이동할 주소를 넣어서
	 * 3. /passwordCheck 를 리턴
	 * 4. passwordCheck에서 로그인된 아이디랑 비번일 일치하면
	 * 5. nextpage이름으로 세션을 만들고 이동할 주소를 넣어서
	 * 6. 처음에 접근한 주소로 리다이렉트 시킴
	 * 7. 세션에 nextpage라는 이름이 있으면 세션을지우고 해당페이지로 이동시킴
	 */
	@RequestMapping("/pwcheck")
	public String pwcheckPro(HttpServletRequest request, Model model, UserVO vo, String nextpage) {
		if(request.getSession().getAttribute("id") != null) {
			String id = vo.getId();
			if(adminCheck(request)) {
				vo.setId(request.getSession().getAttribute("id").toString());
				vo = dao.login(vo);
				if(vo.getRole() != null) {
					vo.setId(id);
					vo = dao.userGetone(vo);
					request.getSession().setAttribute("nextpage", nextpage);
					
					return "redirect:/" + nextpage;
				} else {
					model.addAttribute("err", 1);
					return "redirect:/" + nextpage;
				}
			} else {
				vo = dao.login(vo);
				if(vo.getRole() != null) {
					request.getSession().setAttribute("nextpage", nextpage);
					return "redirect:/" + nextpage;
				} else {
					model.addAttribute("err", 1);
					return "redirect:/" + nextpage;
				}
			}
			
		}
		return "/user/login";
	}
	
//	내정보
	@RequestMapping(value = "/myinfo", method = RequestMethod.GET)
	public String myinfo(HttpServletRequest request, Model model) {
		if(request.getSession().getAttribute("id") != null) {
			String id = request.getSession().getAttribute("id").toString();
			UserVO vo = new UserVO();
			vo.setId(id);
			vo = dao.userGetone(vo);
			model.addAttribute("userinfo", vo);
			request.getSession().setAttribute("nextid", vo.getId());
			return "/user/myinfo";
		}
		return "/user/login";
	}
	
//	회원정보수정 화면
	@RequestMapping("/userUpdate")
	public String userUpdate(HttpServletRequest request, UserVO vo, Model model) {
		
		if(request.getSession().getAttribute("id") != null) {
//			관리자
//			if(adminCheck(request)) {
//				vo = dao.userGetone(vo);
//				model.addAttribute("userinfo", vo);
//				return "/user/userUpdate";
//			}
			if(request.getSession().getAttribute("nextpage") != null) {
				String nextpage = request.getSession().getAttribute("nextpage").toString();
				if(nextpage.equals("userUpdate")) {
					String id = request.getSession().getAttribute("nextid").toString();
					vo.setId(id);
					request.getSession().removeAttribute("nextpage");
					
					vo = dao.userGetone(vo);
					model.addAttribute("userinfo", vo);
					return "/user/userUpdate";
				}
			}
			if(request.getSession().getAttribute("nextid") == null) {
				request.getSession().setAttribute("nextid", vo.getId());
			}
			model.addAttribute("userinfo", vo);
			model.addAttribute("nextpage", "userUpdate");
			return "/passwordCheck";
			
		} else {
			return "redirect:/";
		}
	}
	
//	회원정보 수정 
	@RequestMapping(value = "/userUpdatePro", method = RequestMethod.POST)
	public String userUpdatePro(HttpServletRequest request, UserVO vo, Model model) {
		if(vo.getRole() == null) {
			vo.setRole("user");
		}
		dao.editUser(vo);
		request.getSession().removeAttribute("nextid");
		if(adminCheck(request)) {
			if(request.getSession().getAttribute("id").equals(vo.getId()) && vo.getRole().equals("user")) {
				return "redirect:/logout";
			}
			return "redirect:/userList";
		}
		model.addAttribute("nextpage", "myinfo");
		request.getSession().setAttribute("nextpage", "myinfo");
		return "redirect:/myinfo";
		
	}
	
//	회원탈퇴
	@RequestMapping("/deleteuser")
	@ResponseBody
	public String deleteUser(HttpServletRequest request, UserVO vo, Model model) {
		String result = "";
		if(request.getSession().getAttribute("id") != null) {
			ReservationDAO rDAO = ReservationDAO.getInstance();
			rDAO.reservationDeleteUser(vo);
			dao.deleteUser(vo);
			if(adminCheck(request)) {
				result = "admin";
				return result;
			}
			request.getSession().invalidate();
			result = "1";
		}
		return result;
	}
	
//	아이디찾기 화면
	@RequestMapping(value = "/findId", method = RequestMethod.GET)
	public String findId() {
		return "/user/findId";
	}
	
//	아이디찾기
	@RequestMapping(value = "/findId", method = RequestMethod.POST)
	public String findId(UserVO vo, Model model) {
		
		List<String> idList = dao.findId(vo);
		if(idList.size() == 0) {
			model.addAttribute("err", 1);
			return "redirect:/findId";
		}
		model.addAttribute("idlist", idList);
		
		return "/user/findIdResult";
	}
	
//	비밀번호찾기 화면
	@RequestMapping(value = "/findPw", method = RequestMethod.GET)
	public String findPw() {
		return "/user/findPw";
	}
	
	/* 비밀번호찾기
	 * 비밀번호 찾기에서 아이디, 이름, 이메일을 입력해서
	 * 데이터베이스에 일차하는 값이 있으면
	 * 아이디를 세션에 changeID라는 이름으로 저장후
	 * 비밀번호 변경하는 화면으로 이동
	 * 비밀번호를 변경하는 화면에서 새 비밀번호를 입력받아서
	 * 세션에 있는 아이디의 비밀번호를 변경
	 */
	@RequestMapping(value = "/findPw", method = RequestMethod.POST)
	public String findPw(HttpServletRequest request, UserVO vo, Model model) {

		String pw = dao.findPw(vo);
		if(pw.isEmpty()) {
			model.addAttribute("err", 1);
			return "redirect:/findPw";
		}

		request.getSession().setAttribute("changeID", vo.getId());
		return "/user/changePw";
	}
	
//	비밀번호변경
	@RequestMapping(value = "/changePw", method = RequestMethod.POST)
	@ResponseBody
	public String changePw(HttpServletRequest request, HttpServletResponse response, UserVO vo, Model model){
		String changeID = request.getSession().getAttribute("changeID").toString();
		vo.setId(changeID);
		request.removeAttribute("changeID");
		String result =  dao.changePw(vo);
		if(adminCheck(request)) {
			result = "admin";
		}
		return result;
	}
	
//	유저목록
	@RequestMapping("/userList")
	public String userList(HttpServletRequest request, Model model) {
		if(adminCheck(request)) {
			
			request.getSession().removeAttribute("nextid");
			
			List<UserVO> userList = new ArrayList<UserVO>();
			userList = dao.userGetall();
			
			model.addAttribute("userList", userList);
			return "/user/userList";
		}
		return "redirect:/myinfo";
	}
	
//	유저 정보상세
	@RequestMapping("/userInfo")
	public String userInfo(HttpServletRequest request, UserVO vo, Model model) {
		if(adminCheck(request)) {
			vo = dao.userGetone(vo);
			model.addAttribute("userinfo", vo);
			request.getSession().setAttribute("nextid", vo.getId());
			return "/user/myinfo";
		}
		return "redirect:/";
	}
	
//	관리자 권한확인
	private boolean adminCheck(HttpServletRequest request) {
		boolean flag = false;
		String role = "";
		if(request.getSession().getAttribute("role") != null) {
			role = request.getSession().getAttribute("role").toString();
			if(role.equals("admin")) {
				flag = true;
			}
		}
		return flag;
	
	}
	
}