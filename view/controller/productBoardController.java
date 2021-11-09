

/* productBoardController 기능
 * 
 * 1. getProductList 등록되있는 상품의 전체목록을 가져와서 화면에 출력
 * 2. getProduct 선택한 상품의 상세정보를 가져와서 화면에 출력
 * 3. productDelete 선택한 상품삭제 
 * 4. productUpdate 상품 정보를 변경하기전 해당상품의 원래정보를 가져와서 화면에 출력
 * 5. productUpdatePro 상품정보 변경
 * 6. productReservation 로그인한 유저 정보와 선택한 상품 정보를 가져와서 화면에 출력
 * 7. returnReservationTime 예약할 방 번호와 날짜를 받아서 예약현황을 전송
 * 8. productReservationPro 유저정보 예약할 방정보 예약할 날짜 시간 등을 받아서 예약실행
 * 
 * */

package com.iwork.view.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.iwork.biz.product.ProductDAO;
import com.iwork.biz.product.ProductVO;
import com.iwork.biz.reservation.ReservationVO;
import com.iwork.biz.user.UserDAO;
import com.iwork.biz.user.UserVO;


@Controller
public class productBoardController {

	private ProductDAO productDAO = ProductDAO.getInstance();
	
	//상품 목록
	@RequestMapping("/product")
	public String getProductList(Model model) {
		List<ProductVO> productList = productDAO.getProductList();
		
		for(ProductVO p : productList) {
			if(p.getImgname() != null) {
				
//		데이터베이스에있는 이미지 이름을 ','를 기준으로 잘라서 배열로 만듬
				String[] img = p.getImgname().split(",");
				p.setImgList(img);
			}
		}
		
		model.addAttribute("productList", productList);
		return "/product/productList";
	}
	
	//상품 상세
	@RequestMapping("/product/room")
	public String getProduct(Model model, ProductVO vo) {
		ProductVO product = productDAO.getProduct(vo);
		
		model.addAttribute("product", product);
		return "/product/product";
	}
	
	//상품 등록화면
	@RequestMapping(value = "/product/productInsert", method = RequestMethod.GET)
	public String productInser(HttpServletRequest request) {
		if(adminCheck(request)){
			return "/product/productInsert";
		}
		return "redirect:/product";
	}
	
	//상품 등록
	@RequestMapping(value = "/product/productInsert", method = RequestMethod.POST)
	public String productInsertPro(ProductVO vo, HttpServletRequest request) throws IOException {
		
		if(adminCheck(request)) {
			MultipartFile[] imgname = vo.getImg();
			if(imgname.length != 0) {
				for(int i = 0; i < imgname.length; i++) {
					String realPath = request.getSession().getServletContext().getRealPath("resources/images/");
					System.out.println("파일 저장 경로 : " + realPath);
					
					if(!imgname[i].isEmpty()) {
						String fileName = imgname[i].getOriginalFilename();
						UUID uuid = UUID.randomUUID();
						String saveName = uuid.toString() + fileName;

						if(vo.getImgname() == null) {
							vo.setImgname(saveName);
						} else {
							vo.setImgname(vo.getImgname() + "," + saveName);
						}
						
						imgname[i].transferTo(new File(realPath + saveName));
					}
					
				}
			}
			productDAO.insertProduct(vo);
		}
		return "redirect:/product";
	}
	
	//상품삭제
	@RequestMapping("/product/productDelete")
	public String productDelete(ProductVO pvo, ReservationVO rvo, HttpServletRequest request) {

		if(adminCheck(request)) {
			String realPath = request.getSession().getServletContext().getRealPath("resources/images/");
			
			if(productDAO.getFileName(pvo).getImgname() != null) {
				String[] imgName = productDAO.getFileName(pvo).getImgname().split(",");
				for(String s : imgName) {
					File deleteFile = new File(realPath + s);
					
					if(deleteFile.exists()) {
						deleteFile.delete();
					}
				}
			}
			
			productDAO.deleteReservation(rvo);
			productDAO.productDelete(pvo);
		}
		return "redirect:/product";
	}
	
	//상품 업데이트 화면
	@RequestMapping(value = "/product/productUpdate", method = RequestMethod.GET)
	public String productUpdate(Model model, ProductVO vo, HttpServletRequest request){

		if(adminCheck(request)) {
			ProductVO product = productDAO.getProduct(vo);
			if(product.getImgname() != null) {
				
				String[] imgName = product.getImgname().split(",");
				
				model.addAttribute("imgname", imgName);
			}
			model.addAttribute("product", product);
			return "/product/productUpdate";
		} else {
			return "redirect:/product";
		}
	}
	
	//상품 업데이트 실행
	@RequestMapping(value = "/product/productUpdatePro", method = RequestMethod.POST)
	public String productUpdatePro(ProductVO vo, HttpServletRequest request) throws IOException {
		if(adminCheck(request)) {
			MultipartFile[] imgname = vo.getImg();
			
//			사진파일이 있을경우
			vo.setImgname(productDAO.getFileName(vo).getImgname());
			if(imgname != null) {
				for(int i = 0; i < imgname.length; i++) {
					String realPath = request.getSession().getServletContext().getRealPath("resources/images/");
					System.out.println("파일 저장 경로 : " + realPath);
					
					if(!imgname[i].isEmpty()) {
						String fileName = imgname[i].getOriginalFilename();
						UUID uuid = UUID.randomUUID();
						String saveName = uuid.toString() + fileName;

						if(vo.getImgname() == null) {
							vo.setImgname(saveName);
						} else {
							vo.setImgname(vo.getImgname() + "," + saveName);
						}
						
						imgname[i].transferTo(new File(realPath + saveName));
					}
					
				}
			}
//			파일 없을경우
			else {
				vo.setImgname(productDAO.getFileName(vo).getImgname());
			}
			productDAO.productUpdate(vo);
		}
		return "redirect:/product";
	}
	
	//상품예약 화면
	@RequestMapping(value = "/product/productReservation")
	public String productReservation(ProductVO product, Model model) {
		product = productDAO.getProduct(product);
//		ReservationVO vo = new ReservationVO();
//		vo.setPnum(product.getPnum());
		model.addAttribute("product", product);
		return "/product/productReservation";
	}
	
	//예약할 날짜와 방번호를 전송받아서 시간별 예약현황전송
	@RequestMapping(value="/product/reservationTime", method = RequestMethod.POST)
	public String returnReservationTime(String date, int pnum, Model model) {
		ReservationVO vo = new ReservationVO();
		vo.setPnum(pnum);
		Date setDate = Date.valueOf(date);
		vo.setRdate(setDate);
		
		//방번호와 예약할 날짜를 받아서 예약현황을 리스트로 반환
		List<ReservationVO> reservationVO = productDAO.reservationTimeList(vo);
		//예약현황 리스트를 배열로 변경
		int[] rt = productDAO.reservationTimeArr(reservationVO);
		
		model.addAttribute("rt", rt);
		
		return "/product/reservationTime";
	}
	
	//상품예약 실행
	@RequestMapping(value="/product/productReservationPro", method = RequestMethod.POST)
	@ResponseBody
	public String productReservationPro(String date, int pnum, int starttime, int endtime, String id, HttpServletRequest requst) throws IOException {
		UserVO uvo = new UserVO();
		ProductVO pvo = new ProductVO();
		ReservationVO rvo = new ReservationVO();

		uvo.setId(requst.getSession().getAttribute("id").toString());
		UserDAO userDAO = new UserDAO();
		//전화번호 가져옴
		String tel = String.valueOf(userDAO.userGetone(uvo).getTel());
		//전화번호 뒤 4자리 추출
		tel = tel.substring(tel.length()-4, tel.length());
		
		uvo.setTel(tel);
		
		rvo.setPnum(pnum);
		Date setDate = Date.valueOf(date);
		rvo.setRdate(setDate);
		
		if(endtime == 0){
			endtime = starttime+1;
		}
		
		rvo.setStarttime(String.valueOf(starttime));
		rvo.setEndtime(String.valueOf(endtime));
		
		boolean flag = productDAO.reservationProduct(uvo, pvo, rvo);
		if(!flag) {
			return "2";
		}
		return "1";
	}
	
//	사진파일 삭제
	@RequestMapping(value = "/product/deleteFile", method = RequestMethod.POST)
	@ResponseBody
	public void deleteFile(ProductVO vo, String imgname,  HttpServletRequest request) {
		String realPath = request.getSession().getServletContext().getRealPath("resources/images/");
		String[] imgList = productDAO.getFileName(vo).getImgname().split(",");
		vo = productDAO.getProduct(vo);
		vo.setImgname("");
		for(int i = 0; i < imgList.length; i++) {
			if(imgList[i].equals(imgname)) {
				File deleteFile = new File(realPath + imgname);
				//파일이 유무 확인
				if(deleteFile.exists()) {
					//파일이 있으면 삭제
					deleteFile.delete();
				}
			} else {
				if(vo.getImgname().equals("")) {
					vo.setImgname(imgList[i]);
				} else {
					vo.setImgname(vo.getImgname() + "," +  imgList[i]);
				}
			}
			
		}
		System.out.println("파일이름 : " + vo.getImgname());
		productDAO.productUpdateImg(vo);
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
