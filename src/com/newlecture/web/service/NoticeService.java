package com.newlecture.web.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.newlecture.web.entity.Notice;
import com.newlecture.web.entity.NoticeView;

public class NoticeService {
	
	//몇개가 삭제되었는지 갯수반환 (정수형),
	//아이디를 배열로넘겨받아 넘겨받은 배열을 이용해 삭제를하는 로직구현 
	public int removeNoticeAll(int[] ids){
		return 0;
	}
	
	//몇개가 공개되었는지
	public int pubNoticeAll(int[] oids, int[] cids){
		
		List<String> oidsList = new ArrayList<>();
		for(int i=0; i<oids.length ; i++)
		oidsList.add(String.valueOf(oids[i]));
		
		List<String> cidsList = new ArrayList<>();
		for(int i=0; i<cids.length ; i++)
		cidsList.add(String.valueOf(cids[i]));
		
		return pubNoticeAll(oidsList,cidsList);
	}
	
	public int pubNoticeAll(List<String> oids, List<String> cids){
		
		String oidsCSV = String.join(",",oids);
		String cidsCSV = String.join(",", cids);
		
		return pubNoticeAll(oidsCSV,cidsCSV);
	}
	
	// 20,30,43,56 콤마로 구분된 문자열형태도 보낼수있게
	public int pubNoticeAll(String oidsCSV, String cidsCSV){ //CSV : 콤마로 구분된값
		
		int result=0;
		
		String sqlOpen = String.format("UPDATE NOTICE SET PUB=1 WHERE ID IN (%s)",oidsCSV);
		String sqlClose = String.format("UPDATE NOTICE SET PUB=0 WHERE ID IN (%s)",cidsCSV);
		
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			
			Statement stOpen = con.createStatement();
			result += stOpen.executeUpdate(sqlOpen);
			
			Statement stClose = con.createStatement();
			result += stOpen.executeUpdate(sqlClose);
			
			stOpen.close();
			stClose.close();
			con.close();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
			return result; // 몇개를 삭제했는지 반환
	}
	
	
	//추가가 되었으면 1 반환아니면 0을 반환
	//인자값은 Notice객체
	public int insertNotice(Notice notice){
		int result=0;
	
		String sql = "INSERT INTO NOTICE(TITLE, CONTENT, WRITER_ID, PUB, FILES) VALUES(?,?,?,?,?)";
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			PreparedStatement st = con.prepareStatement(sql);	
			st.setString(1, notice.getTitle());
			st.setString(2, notice.getContent());
			st.setString(3, notice.getWriterId());
			st.setBoolean(4, notice.getPub());
			st.setString(5, notice.getFiles());
			result = st.executeUpdate();
			
			st.close();
			con.close();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return result; // 몇개를 삭제했는지 반환
	}
	
	//삭제가 되었으면 1 반환 아니면 0을반환
	public int deleteNotice(int id){
		return 0;
	}
	
	//수정이 되었으면 1 반환 아니면 0을반환
	public int updateNotice(Notice notice){
		return 0;
	}
	
	//댓글수를 알아볼필요가없으니까 List<Notice>륿 반환값으로 가진다.
	List<Notice> getNoticeNewestList(){
		return null;
	}
	
	public List<NoticeView> getNoticeList(){		
		return getNoticeList("title","",1);
	}
	public List<NoticeView> getNoticeList(int page){	
		return getNoticeList("title","",page);
	}

	public List<NoticeView> getNoticeList(String field/*TITLE,WRITER_ID*/, String query/*A*/, int page){
		
		List<NoticeView> list = new ArrayList<>();
		
		String sql ="SELECT * FROM ("
				+ "	SELECT ROWNUM NUM , N.* "
				+ "	FROM (SELECT * FROM NOTICE_VIEW WHERE "+field+" LIKE ? ORDER BY REGDATE DESC) N"
				+ "	) "
				+ "	WHERE NUM BETWEEN ? AND ?";
		
		// 1 , 11 , 21 , 31 -> an = a1+(n-1)*d 
		                          // 1+(page-1)*10
		//10 , 20 , 30 , 40 -> page*10
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			PreparedStatement st = con.prepareStatement(sql);
			
			// WHERE문에 field를 ? 넣게되면 'TITLE' 이런식으로 들어가기때문에 ? 사용하지못한다.
			st.setString(1, "%"+query+"%");
			st.setInt(2, 1+(page-1)*10);
			st.setInt(3, page*10);			
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()){
				
				int id = rs.getInt("ID");
				String title = rs.getString("TITLE"); //모델 변수
				Date regdate = rs.getDate("REGDATE");
				String writerId = rs.getString("WRITER_ID");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				//String content = rs.getString("CONTENT");
				int cmtCount = rs.getInt("CMT_COUNT");
				boolean pub = rs.getBoolean("PUB");
				
			NoticeView notice = new NoticeView(
					id,
					title,
					regdate,
					writerId,
					hit,
					files,
					//content
					cmtCount,
					pub
					);

			list.add(notice);

			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return list;
	}
	
	public List<NoticeView> getNoticePubList(String field, String query, int page) {
		List<NoticeView> list = new ArrayList<>();
		
		String sql ="SELECT * FROM ("
				+ "	SELECT ROWNUM NUM , N.* "
				+ "	FROM (SELECT * FROM NOTICE_VIEW WHERE "+field+" LIKE ? ORDER BY REGDATE DESC) N"
				+ "	) "
				+ "	WHERE PUB=1 AND NUM BETWEEN ? AND ?";
		
		// 1 , 11 , 21 , 31 -> an = a1+(n-1)*d 
		                          // 1+(page-1)*10
		//10 , 20 , 30 , 40 -> page*10
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			PreparedStatement st = con.prepareStatement(sql);
			
			// WHERE문에 field를 ? 넣게되면 'TITLE' 이런식으로 들어가기때문에 ? 사용하지못한다.
			st.setString(1, "%"+query+"%");
			st.setInt(2, 1+(page-1)*10);
			st.setInt(3, page*10);			
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()){
				
				int id = rs.getInt("ID");
				String title = rs.getString("TITLE"); //모델 변수
				Date regdate = rs.getDate("REGDATE");
				String writerId = rs.getString("WRITER_ID");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				//String content = rs.getString("CONTENT");
				int cmtCount = rs.getInt("CMT_COUNT");
				boolean pub = rs.getBoolean("PUB");
				
			NoticeView notice = new NoticeView(
					id,
					title,
					regdate,
					writerId,
					hit,
					files,
					//content
					cmtCount,
					pub
					);

			list.add(notice);

			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return list;
	}
	
	
	
	public int getNoticeConunt() {
		return getNoticeCount("title","");
	}
	public int getNoticeCount(String field,String query) {
		
		int count = 0;
		
		String sql ="SELECT COUNT(ID) COUNT FROM ("
				+ "	SELECT ROWNUM NUM , N.* "
				+ "	FROM (SELECT * FROM NOTICE WHERE "+field+" LIKE ? ORDER BY REGDATE DESC) N"
				+ ")";
		
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			PreparedStatement st = con.prepareStatement(sql);
			
			
			st.setString(1, "%"+query+"%");		
			
			ResultSet rs = st.executeQuery();
			
			if(rs.next()) {
			count = rs.getInt("count");
			}
			

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;		
	}
	
	
	
	
	
	public Notice getNotice(int id) {
		Notice notice = null;
		
		String sql = "SELECT * FROM NOTICE WHERE ID=?";
		
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			PreparedStatement st = con.prepareStatement(sql);
			
			// WHERE문에 field를 ? 넣게되면 'TITLE' 이런식으로 들어가기때문에 ? 사용하지못한다.
			st.setInt(1, id);	
			
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){
				
				int nid = rs.getInt("ID");
				String title = rs.getString("TITLE"); //모델 변수
				Date regdate = rs.getDate("REGDATE");
				String writerId = rs.getString("WRITER_ID");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				String content = rs.getString("CONTENT");
				boolean pub = rs.getBoolean("PUB");
				
			notice = new Notice(
					nid,
					title,
					regdate,
					writerId,
					hit,
					files,
					content,
					pub
					);

			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return notice;
	}
	
	public Notice getNextNotice(int id) {
		Notice notice = null;
		String sql = "SELECT * FROM (SELECT * FROM NOTICE ORDER BY REGDATE ASC)"
				+ "	WHERE ID = ("
				+ "	SELECT ID FROM NOTICE "
				+ "	WHERE REGDATE > (SELECT REGDATE FROM NOTICE WHERE ID=?)"
				+ "	AND ROWNUM = 1"
				+ ")";
		
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			PreparedStatement st = con.prepareStatement(sql);
			
			// WHERE문에 field를 ? 넣게되면 'TITLE' 이런식으로 들어가기때문에 ? 사용하지못한다.
			st.setInt(1, id);	
			
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){
				
				int nid = rs.getInt("ID");
				String title = rs.getString("TITLE"); //모델 변수
				Date regdate = rs.getDate("REGDATE");
				String writerId = rs.getString("WRITER_ID");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				String content = rs.getString("CONTENT");
				boolean pub = rs.getBoolean("PUB");
					
			notice = new Notice(
					nid,
					title,
					regdate,
					writerId,
					hit,
					files,
					content,
					pub
					);
			}
			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return notice;
	}
	public Notice getPrevNotice(int id) {
		Notice notice = null;
		String sql = "SELECT * FROM NOTICE "
				+ "WHERE ID = ("
				+ "    SELECT ID FROM (SELECT * FROM NOTICE ORDER BY REGDATE DESC)"
				+ "    WHERE REGDATE < (SELECT REGDATE FROM NOTICE WHERE ID=?)"
				+ "    AND ROWNUM = 1"
				+ "    )";
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			PreparedStatement st = con.prepareStatement(sql);	
			// WHERE문에 field를 ? 넣게되면 'TITLE' 이런식으로 들어가기때문에 ? 사용하지못한다.
			st.setInt(1, id);		
			ResultSet rs = st.executeQuery();
			if(rs.next()){		
				int nid = rs.getInt("ID");
				String title = rs.getString("TITLE"); //모델 변수
				Date regdate = rs.getDate("REGDATE");
				String writerId = rs.getString("WRITER_ID");
				int hit = rs.getInt("HIT");
				String files = rs.getString("FILES");
				String content = rs.getString("CONTENT");
				boolean pub = rs.getBoolean("PUB");
				
			notice = new Notice(
					nid,
					title,
					regdate,
					writerId,
					hit,
					files,
					content,
					pub
					);

			}

			rs.close();
			st.close();
			con.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return notice;
	}

	public int deleteNoticeALL(int[] ids) {
		
		int result=0;
		
		String params = "";
		
		for(int i=0;i<ids.length;i++) {
			params += ids[i];	
			if(i < ids.length-1) { //마지막은 ( , ) 가 안붙게
				params += ",";
			}
		} 
		
		
		String sql = "DELETE NOTICE WHERE ID IN ("+params+")";//쉼표로 배열을 prepared 할수없다.
		String url = "jdbc:oracle:thin:@localhost:1521:xe";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url,"newlec","1234");
			Statement st = con.createStatement();	
			result = st.executeUpdate(sql);
			
			st.close();
			con.close();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return result; // 몇개를 삭제했는지 반환
	}

	
	
	
}
