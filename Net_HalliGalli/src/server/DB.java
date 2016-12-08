package server;

import java.sql.*;
import java.util.Scanner;

public class DB {

	public static String id,pwd;
	
	static Connection con=null;//db접속

	
	public DB(String id,String pwd) throws SQLException, ClassNotFoundException {
		this.id=id;
		this.pwd=pwd;
		
		Class.forName("com.mysql.jdbc.Driver");//mysql driver 부름
		con=DriverManager.getConnection("jdbc:mysql://localhost/HalliGalli","root","12345");//커넥션 정보 넘기며 커넥션 얻음
		
	}

	public static void insert() throws SQLException {
		
		PreparedStatement stmt1=null;
		Statement stmt2=null;//질의문 실행
		PreparedStatement stmt3=null;
		PreparedStatement stmt4=null;
		ResultSet result=null;//결과 클래스
		
		//먼저 mysql열어서 .sql파일 열기

		
		try{
			
			
			stmt2=con.createStatement();		
			String query="INSERT INTO CLIENT(ID, PASSWORD) VALUES('"+id+"', '"+pwd+"')";
			stmt2.executeUpdate(query);//쿼리 실행 (update,insert,delete)
			
			
			stmt3=con.prepareStatement("SELECT ID,PASSWORD FROM CLIENT");
			result=stmt3.executeQuery();//쿼리 실행(select)
			
			
			//이미지 파일 클라이언트한테 다운받아와서 이미지 파일 경로 DB에 저장하기
			while(result.next()){
				String g_id=result.getString("ID");//테이블에서 값 받아오기
				String g_pwd=result.getString("PASSWORD");	
				System.out.println("ID: "+g_id+" password: "+g_pwd);
			}
			
			con.setAutoCommit(false);
			con.commit();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(stmt1!=null)
				stmt1.close();
			if(con!=null)
				con.close();
			if(stmt2!=null)
				stmt2.close();
			if(result!=null)
				result.close();
		}		
		
		
	}
	
	
	public static int checkID(String id) throws SQLException {
		
		PreparedStatement stmt1=null;
		Statement stmt2=null;//질의문 실행
		PreparedStatement stmt3=null;
		PreparedStatement stmt4=null;
		ResultSet result=null;//결과 클래스
		
		String g_id=null;
		
		//먼저 mysql열어서 .sql파일 열기

		
		try{
			
			stmt3=con.prepareStatement("SELECT ID FROM CLIENT WHERE ID='"+id+"'");
			result=stmt3.executeQuery();//쿼리 실행(select)
			
			
			//이미지 파일 클라이언트한테 다운받아와서 이미지 파일 경로 DB에 저장하기
			while(result.next()){
				g_id=result.getString("ID");//테이블에서 값 받아오기		
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(stmt1!=null)
				stmt1.close();
			if(con!=null)
				con.close();
			if(stmt2!=null)
				stmt2.close();
			if(result!=null)
				result.close();
		}
		
		
		if(g_id==null)//중복 없음
			return 1;
		else
			return -1;
	}
	
	
	public static int checkPWD(String pwd) throws SQLException {
		
		PreparedStatement stmt1=null;
		Statement stmt2=null;//질의문 실행
		PreparedStatement stmt3=null;
		PreparedStatement stmt4=null;
		ResultSet result=null;//결과 클래스
		
		String g_pwd=null;
		
		//먼저 mysql열어서 .sql파일 열기

		
		try{
			
			stmt3=con.prepareStatement("SELECT PASSWORD FROM CLIENT WHERE PASSWORD='"+pwd+"'");
			result=stmt3.executeQuery();//쿼리 실행(select)
			
			
			while(result.next()){
				g_pwd=result.getString("PASSWORD");//테이블에서 값 받아오기		
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(stmt1!=null)
				stmt1.close();
			if(con!=null)
				con.close();
			if(stmt2!=null)
				stmt2.close();
			if(result!=null)
				result.close();
		}
		
		
		if(g_pwd==null)//중복 없음
			return 1;
		else
			return -1;
	}
	



}

