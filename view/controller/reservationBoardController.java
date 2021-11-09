package com.iwork.view.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iwork.biz.product.ProductDAO;
import com.iwork.biz.reservation.ReservationDAO;
import com.iwork.biz.reservation.ReservationVO;
import com.iwork.biz.user.UserVO;

@Controller
public class reservationBoardController {

	//import
	ReservationDAO reservationDAO = ReservationDAO.getInstance();
	ProductDAO productDAO = ProductDAO.getInstance();
	
	// 예약목록 불러오기 기능 
	@RequestMapping("/reservation")
	public String getAllReservationList(UserVO VO, Model model, HttpServletRequest request){
		String id = "";
		if(request.getSession().getAttribute("id") != null && !adminCheck(request)) {
			id = request.getSession().getAttribute("id").toString();
			VO.setId(id);
		}
		List<ReservationVO> ReservationList = reservationDAO.reservationList(VO);
		
		model.addAttribute("reservationList", ReservationList);
		return "/reservation/reservationList";
	}
	
	//예약 삭제 기능
	@RequestMapping("/reservationDelete")
	public String reservationDelete(HttpServletRequest request, ReservationVO vo, Model model) {
		reservationDAO.reservationDelete(vo);
//		관리가일 경우 실행
		if(adminCheck(request)) {
//			이전페이지 주소를 가져옴
			String url = request.getHeader("REFERER");
//			이전페이지에서 '/'문자의 왼쪽을 자름
			String urlCut = url.substring(url.lastIndexOf("/")+1);
//			'?'가 몇번째에 있는지 확인
			int index = urlCut.indexOf("?");
//			'?'포함해서 우측에 있는문자를 자름
			String result = urlCut.substring(0, index);
			/*
			 * 관리자가 예약을 취소할수있는 경로는
			 * 예약목록에서 취소하는방법과
			 * 유저리스트에서 취소하는방법 2가지
			 * 예약목록에서 취소한경우는 예약목록으로 돌아가고
			 * 유저리스트에서 취소한 경우는 유저리스트로 돌아가게 하기위해 if문 사용
			 * 
			 */
			if(result.equals("reservation")) {
//				유저리스트에서 취소한경우
				return "redirect:/" + urlCut;
			} else {
//				예약목록에서 취소한 경우
				return "redirect:/reservationAdmin_detail?pnum=" + vo.getPnum();
			}
		} else {
//			일반유저가 취소한 경우
			return "redirect:/reservation"; 
		}
	}
	
	//관리자 페이지
	@RequestMapping("/reservationAdmin")
	public String reservationAdmin(Model model) {
		List<ReservationVO> ReservationAdmin = reservationDAO.reservationAdminList();
		model.addAttribute("reserveAdmin", ReservationAdmin);
		
		return "/reservation/reservation_Admin";
	}
	
	//관리자 예약 상세 페이지
	@RequestMapping("/reservationAdmin_detail")
	public String reservationAdmin_detail(Model model, int pnum) {
		List<ReservationVO> ReservationAdmin_detail = reservationDAO.reservationAlist(pnum);
		model.addAttribute("reserveAlist", ReservationAdmin_detail);
		
		return "/reservation/reservation_AdminDetail";
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




