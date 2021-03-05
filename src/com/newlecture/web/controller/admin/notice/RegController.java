package com.newlecture.web.controller.admin.notice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.newlecture.web.entity.Notice;
import com.newlecture.web.service.NoticeService;

@MultipartConfig( 
	//	location="/tmp",  //디스크 일반적으로 설정을안함 (기본값"" 사용)
		fileSizeThreshold = 1024*1024, //전송하는데이터가 이 크기를 넘어갈때 디스크를사용
		maxFileSize = 1024*1024*50, // 하나의 파일 사이즈의 최대값
		maxRequestSize = 1024*1024*50*5 // 전체 요청에대한 파일 사이즈 최대값 
	)
@WebServlet("/admin/board/notice/reg")
public class RegController extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request
		.getRequestDispatcher("/WEB-INF/view/admin/board/notice/reg.jsp")
		.forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//기본값은 정해주지않아도된다 전부 전달받아 오기때문에
		String title = request.getParameter("title");
		
		System.out.println("title : ");
		System.out.println(title);
		
		String content = request.getParameter("content");
		String isOpen = request.getParameter("open"); // (t/f)
		
		Collection<Part> parts = request.getParts();
		StringBuilder builder = new StringBuilder();
		
		for(Part p : parts) {
			
			if(!p.getName().equals("file")) continue;
			if(p.getSize() == 0 ) continue;
			
			Part filePart = p;
			String fileName = filePart.getSubmittedFileName();
			//System.out.println(fileName);
			builder.append(fileName);
			builder.append(",");
			
			
			InputStream fis = filePart.getInputStream(); // 바이널을 받는방법
			
	
			String realPath = request.getServletContext().getRealPath("/member/upload");//물리경로를 알려주는 함수
			System.out.println(realPath);
			
			File path = new File(realPath);
			if(!path.exists())
				path.mkdirs();
			
			//중복된 fileName 마감처리
			String filePath = realPath + File.separator + fileName;
			FileOutputStream fos = new FileOutputStream(filePath);
			
			byte[] buf = new byte[1024];
			int size = 0 ;
			while((size = fis.read(buf)) != -1 ) {
				fos.write(buf, 0, size);
			}
			fos.close();
			fis.close();
		
		}
		
		if(builder.length()!=0)
		builder.delete(builder.length()-1,builder.length());
		
		
		boolean pub = false;
		if(isOpen != null) {
			pub = true;
		}
		
		Notice notice = new Notice();
		notice.setTitle(title);
		notice.setContent(content);
		notice.setPub(pub);
		notice.setWriterId("newlec"); //(임시방편) 인증과 권한 로그인처리 구현 후 수정
		notice.setFiles(builder.toString());
		
		NoticeService service = new NoticeService();
		service.insertNotice(notice);
		
		
		response.sendRedirect("list");
	}
}
 