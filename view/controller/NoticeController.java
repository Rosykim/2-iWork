

package com.iwork.view.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.iwork.biz.notice.NoticeDAO;
import com.iwork.biz.notice.NoticeVO;
import com.iwork.biz.notice.Pagination;

@Controller
public class NoticeController {

	private NoticeDAO noticeDAO = NoticeDAO.getInstance();
	
//	공지목록
//	@RequestMapping("/noticeList")
//	public String getNoticeList(NoticeVO vo, Model model) {
//		
//		List<NoticeVO> noticeList = noticeDAO.getNoticeList();
//		model.addAttribute("noticeList", noticeList);
//		
//		return "/notice/noticeList";
//	}
	
//	공지목록 + 페이징처리
	@RequestMapping("/notice")
	public String getNoticeList(NoticeVO vo, Model model, int page, HttpServletRequest request) {
		request.getSession().setAttribute("page", page);
		
		int listCnt = noticeDAO.noticeCount();
		Pagination pagination = new Pagination(listCnt, page);
		vo.setStartIndex(pagination.getStartIndex());
		vo.setCntPerPage(pagination.getPageSize());
		
		List<NoticeVO> list = noticeDAO.getNoticeList();
		model.addAttribute("list", list);
		model.addAttribute("listCnt", listCnt);
		model.addAttribute("pagination", pagination);
		model.addAttribute("noticeList", noticeDAO.getNoticeListCnt(vo));
		
		return "/notice/noticeList";
	}
	
//	공지상세보기
	@RequestMapping("/notice/seq")
	public String getNotice(NoticeVO vo, Model model) {
		
		NoticeVO notice = noticeDAO.getNotice(vo);
//		jsp에서 textarea로 입력을 하고 db에 저장을 할때
//		줄바꿈(엔터)가 입력된 채로 저장하는데 이것을 다시 불러올때 공백으로 인식됨
//		따라서 <br>태그로 변경을 해줘서 줄바꿈이 표시되도록 변경
		String[] content = notice.getContent().split("\r\n");
		notice.setContentList(content);
		
		model.addAttribute("notice", notice);
		return "/notice/notice";
	}
	
//	공지작성 화면
	@RequestMapping(value = "/notice/insert", method = RequestMethod.GET)
	public String insertNotice(NoticeVO vo, HttpServletRequest request, Model model) {
		if(adminCheck(request)) {
			return "/notice/noticeInsert";
		}
		return "redirect:/notice";
	}
	
//	공지 작성
	@RequestMapping(value = "/notice/insert", method = RequestMethod.POST)
	public String insertNoticePro(NoticeVO vo, Model model, HttpServletRequest request) {
		request.getSession().setAttribute("page", 1);
		int nseq = 0;
		if(adminCheck(request)) {
			noticeDAO.insertNotice(vo);
			nseq = noticeDAO.getMaxNseq();
		}
		if(nseq != 0) {			
			return "redirect:/notice/seq?nseq=" + nseq;
		} else {
			return "redirect:/notice";
		}
	}
	
//	공지 수정화면
	@RequestMapping(value = "/notice/update", method = RequestMethod.POST)
	public String updateNotice(NoticeVO vo, Model model, HttpServletRequest request) {
		if(adminCheck(request)) {
			vo = noticeDAO.getNotice(vo);
			model.addAttribute("notice", vo);
			return "/notice/noticeUpdate";
		}
		return "redirect:/notice";
	}
	
//	공지 수정
	@RequestMapping(value = "/notice/updatePro", method = RequestMethod.POST)
	public String updateNoticePro(NoticeVO vo, HttpServletRequest request) {
		if(adminCheck(request)) {
			noticeDAO.updateNotice(vo);
			return "redirect:/notice/seq?nseq=" + vo.getNseq();
		}
		return "redirect:/notice";
	}
	
//	공지 삭제
	@RequestMapping("/notice/delete")
	public String deleteNoticePro(NoticeVO vo, HttpServletRequest request, Model model) {
		model.addAttribute("page", 1);
		if(adminCheck(request)) {
			noticeDAO.deleteNotice(vo);
		}
		
		return "redirect:/notice";
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
