package com.newlecture.web.controller.admin.notice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.newlecture.web.entity.NoticeView;
import com.newlecture.web.service.NoticeService;

@WebServlet("/admin/board/notice/list")
public class ListController extends HttpServlet{
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] openIds = request.getParameterValues("open-id"); //체크된 상태
		String[] delIds = request.getParameterValues("del-id");
		String cmd = request.getParameter("cmd");
		String ids_ = request.getParameter("ids");
		String[] ids = ids_.trim().split(" "); // 목록상태
		
		NoticeService service = new NoticeService();
		
		switch(cmd) {
		case "일괄공개" :
			for(String openId : openIds) { //일괄공개
				System.out.printf("open id : %s\n",openId);
			}
			List<String> oids = Arrays.asList(openIds);
			// 1,2,3,4,5,6,7,8,9,10 - 3,5,8
			// 1,2,4,6,7,9,10
			List<String> cids = new ArrayList(Arrays.asList(ids));
			cids.removeAll(oids);
			System.out.println(ids);
			System.out.println(oids);
			System.out.println(cids);
			
			//Transaction 처리
			service.pubNoticeAll(oids,cids); // UPDATE NOTICE SET PUB=1 WHERE ID IN(...)
		    //service.closeNoticeList(csIds); // UPDATE NOTICE SET PUB=0 WHERE ID IN(...)
			break;
		
		case "일괄삭제" :
			
			int[] ids1 = new int[delIds.length];
			for(int i=0; i<delIds.length;i++) {
				ids1[i] = Integer.parseInt(delIds[i]);
			}
			int result = service.deleteNoticeALL(ids1);//정수형배열로 반환하여 보낸다.
			//몇개를 삭제하였는지 알수있는 result변수를 만든다.
			break;
		}
		
		//다시 목록페이지를 보여주게끔 get요청을 한다. post요청 -> get요청 
		response.sendRedirect("list");
	}	
	
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//list?f=title&q=a
		
		String field_ = request.getParameter("f");
		String query_ = request.getParameter("q");
		String page_ = request.getParameter("p");
		//페이지 준비해야할시에 int는 사용할수없다 null을 받을수없는 자료형
		
		//옵션으로 오는 인자값은 null일 경우를 대비하여 기본값을 어떻게 처리해야할것인가를 생각해야한다.
		//null일경우 기본값으로 기본페이지가 보여지게
		String field = "title";
		if(field_ != null && !field_.equals("")) {
			field = field_;
		}
		
		String query ="";
		if(query_ != null && !query_.equals("")) {
			query = query_;
		}
		
		int page = 1;
		if(page_ != null && !page_.equals("")) {
			page = Integer.parseInt(page_);
		}
			
		NoticeService service = new NoticeService();
		List<NoticeView> list = service.getNoticeList(field,query,page);
		int count = service.getNoticeCount(field,query);
		
		request.setAttribute("list",list);
		request.setAttribute("count",count);
		
		request
		.getRequestDispatcher("/WEB-INF/view/admin/board/notice/list.jsp")
		.forward(request, response);
	

	}
}

