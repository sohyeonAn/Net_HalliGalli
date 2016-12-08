package server;

import java.sql.*;
import java.util.Scanner;

public class DB {

	public static String id,pwd;
	
	static Connection con=null;//db����

	
	public DB(String id,String pwd) throws SQLException, ClassNotFoundException {
		this.id=id;
		this.pwd=pwd;
		
		Class.forName("com.mysql.jdbc.Driver");//mysql driver �θ�
		con=DriverManager.getConnection("jdbc:mysql://localhost/HalliGalli","root","12345");//Ŀ�ؼ� ���� �ѱ�� Ŀ�ؼ� ����
		
	}

	public static void insert() throws SQLException {
		
		PreparedStatement stmt1=null;
		Statement stmt2=null;//���ǹ� ����
		PreparedStatement stmt3=null;
		PreparedStatement stmt4=null;
		ResultSet result=null;//��� Ŭ����
		
		//���� mysql��� .sql���� ����

		
		try{
			
			
			stmt2=con.createStatement();		
			String query="INSERT INTO CLIENT(ID, PASSWORD) VALUES('"+id+"', '"+pwd+"')";
			stmt2.executeUpdate(query);//���� ���� (update,insert,delete)
			
			
			stmt3=con.prepareStatement("SELECT ID,PASSWORD FROM CLIENT");
			result=stmt3.executeQuery();//���� ����(select)
			
			
			//�̹��� ���� Ŭ���̾�Ʈ���� �ٿ�޾ƿͼ� �̹��� ���� ��� DB�� �����ϱ�
			while(result.next()){
				String g_id=result.getString("ID");//���̺��� �� �޾ƿ���
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
		Statement stmt2=null;//���ǹ� ����
		PreparedStatement stmt3=null;
		PreparedStatement stmt4=null;
		ResultSet result=null;//��� Ŭ����
		
		String g_id=null;
		
		//���� mysql��� .sql���� ����

		
		try{
			
			stmt3=con.prepareStatement("SELECT ID FROM CLIENT WHERE ID='"+id+"'");
			result=stmt3.executeQuery();//���� ����(select)
			
			
			//�̹��� ���� Ŭ���̾�Ʈ���� �ٿ�޾ƿͼ� �̹��� ���� ��� DB�� �����ϱ�
			while(result.next()){
				g_id=result.getString("ID");//���̺��� �� �޾ƿ���		
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
		
		
		if(g_id==null)//�ߺ� ����
			return 1;
		else
			return -1;
	}
	
	
	public static int checkPWD(String pwd) throws SQLException {
		
		PreparedStatement stmt1=null;
		Statement stmt2=null;//���ǹ� ����
		PreparedStatement stmt3=null;
		PreparedStatement stmt4=null;
		ResultSet result=null;//��� Ŭ����
		
		String g_pwd=null;
		
		//���� mysql��� .sql���� ����

		
		try{
			
			stmt3=con.prepareStatement("SELECT PASSWORD FROM CLIENT WHERE PASSWORD='"+pwd+"'");
			result=stmt3.executeQuery();//���� ����(select)
			
			
			while(result.next()){
				g_pwd=result.getString("PASSWORD");//���̺��� �� �޾ƿ���		
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
		
		
		if(g_pwd==null)//�ߺ� ����
			return 1;
		else
			return -1;
	}
	



}

