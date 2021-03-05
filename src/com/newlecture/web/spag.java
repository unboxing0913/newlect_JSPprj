package com.newlecture.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/spag")
public class spag extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int num = 0;
		String num_ = request.getParameter("n");
		
		if(num_ != null && !num_.equals(""))
			num = Integer.parseInt(num_);
			
		String result;
		
		if(num%2 != 0)
		result="È¦¼ö";
	 	else
		result="Â¦¼ö";

		request.setAttribute("result", result);
		
		String[] names= {"newlec","dragon"};
		request.setAttribute("names",names);
		
		Map<String, Object> notice = new HashMap<String, Object>();
		notice.put("id", 1);
		notice.put("title", "ELÀº ÁÁ¾Æ¿ä");
		request.setAttribute("notice", notice);
		
		//redirect
		//forward
		
		RequestDispatcher dispatcher = 
				request.getRequestDispatcher("spag.jsp");
		dispatcher.forward(request, response);
		
	}
}
