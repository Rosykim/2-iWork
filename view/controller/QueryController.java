package com.iwork.view.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.iwork.biz.query.Pagination;
import com.iwork.biz.query.QueryDAO;
import com.iwork.biz.query.QueryVO;

@Controller
public class QueryController {

		private QueryDAO queryDAO = QueryDAO.getInstance();
		
		
// q&a목록 + 페이징처리
	@RequestMapping("/query")
	public String getQueryList(QueryVO vo,Model model, int page, HttpServletRequest request) {
		request.getSession().setAttribute("page",page);
		
		int listCnt = queryDAO.queryCount();
		Pagination pagination = new Pagination(listCnt, page);
		vo.setStartIndex(pagination.getStartIndex());
		vo.setCntPerPage(pagination.getPageSize());
		
		List<QueryVO> queryList = queryDAO.getQueryListCnt(vo);
		for(int i = 0; i < queryList.size(); i++) {
//			String content = queryList.get(i).getAnswer().replace("\r\n","<br>").replace(" ","&nbsp;");
//			queryList.get(i).setAnswer(content);
			String[] content = queryList.get(i).getAnswer().split("\r\n");
			queryList.get(i).setContentList(content);
		}
		model.addAttribute("pagination", pagination);
		model.addAttribute("queryList", queryList);
		
		return "/query/queryList";
	}
	
//	사용안함
////	q&a상세보기
//	@RequestMapping("/query/seq")
//	public String getQuery(QueryVO vo, Model model) {
//		QueryVO query = queryDAO.getQuery(vo);
////		jsp에서 textarea로 입력을 하고 db에 저장을 할때
////		줄바꿈(엔터)가 입력된 채로 저장하는데 이것을 다시 불러올때 공백으로 인식됨
////		따라서 <br>태그로 변경을 해줘서 줄바꿈이 표시되도록 변경
//		String content = query.getAnswer().replace("\r\n","<br>").replace(" ","&nbsp;");
//		query.setAnswer(content);
//		
//		model.addAttribute("query", query);
//		return "/query/query";
//	}
	
//	q&a 작성화면
	@RequestMapping(value = "/query/insert", method = RequestMethod.GET)
	public String insertQuery(QueryVO vo, HttpServletRequest request, Model model) {
		if(adminCheck(request)) {
			return "/query/queryInsert";
		}
		return "redirect:/query";
	}
	
//	q&a 작성
	@RequestMapping(value = "/query/insert", method = RequestMethod.POST)
	public String insertQueryPro(QueryVO vo, Model model, HttpServletRequest request) {
		model.addAttribute("page", 1);
		if(adminCheck(request)) {
			queryDAO.insertQuery(vo);
		}
		return "redirect:/query";

	}
//	q&a 수정화면
	@RequestMapping(value = "/query/update", method = RequestMethod.POST)
	public String updateQuery(QueryVO vo, Model model, HttpServletRequest request) {
		if(adminCheck(request)) {
			vo = queryDAO.getQuery(vo);
			model.addAttribute("query", vo);
			return "/query/queryUpdate";
		}
		return "redirect:/query";
	}
	
//	q&a 수정
	@RequestMapping(value = "/query/updatePro", method = RequestMethod.POST)
	public String updateQueryPro(QueryVO vo, HttpServletRequest request) {
		if(adminCheck(request)) {
			queryDAO.updateQuery(vo);
			String page = request.getSession().getAttribute("page").toString();
			return "redirect:/query?page=" + page;
		}
		return "redirect:/query?page=1";
	}
	
//	q&a 삭제
	@RequestMapping("/query/delete")
	public String deleteQueryPro(QueryVO vo, HttpServletRequest request, Model model) {
		model.addAttribute("page", 1);
		if(adminCheck(request)) {
			queryDAO.deleteQuery(vo);
		}
		
		return "redirect:/query";
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
