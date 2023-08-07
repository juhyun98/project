package bbs;
 
import java.sql.*;
import java.util.*;
 
public class BbsDAO {
    private Connection conn;            // DB에 접근하는 객체
    private ResultSet rs;                // DB data를 담을 수 있는 객체  (Ctrl + shift + 'o') -> auto import
    private PreparedStatement pstmt;
    
    public void getConnection() {
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
	 	
		 	String db_url = "jdbc:oracle:thin:@localhost:1521:xe";
		 	String db_id = "shopping";
			String db_pw = "12345";
			
			conn = DriverManager.getConnection(db_url, db_id, db_pw);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("ojdbc6.jar 또는 경로를 체크하세요!");
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("DB연결에 필요한 정보가 맞는지 체크하세요!");
		}
	}

	public void close() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (conn != null) {
				conn.close();	
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    public BbsDAO(){ 
        try {
        	Class.forName("oracle.jdbc.driver.OracleDriver");
    	 	
		 	String db_url = "jdbc:oracle:thin:@localhost:1521:xe";
		 	String db_id = "shopping";
			String db_pw = "12345";
			
			conn = DriverManager.getConnection(db_url, db_id, db_pw);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	close();
        }
    }
    public String getDate() // 현재시간을 넣어주기위해
    {
    	getConnection();
    	
        String SQL = "SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') FROM DUAL"; // 현재시간을 나타내는 mysql
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                return rs.getString(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	close();
        }
        return ""; // 데이터베이스 오류
    }
    
    public int getNext()
    {
    	getConnection();
        String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC"; // 내림차순으로 가장 마지막에 쓰인 것을 가져온다
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                return rs.getInt(1) + 1; // 그 다음 게시글의 번호
            }
            return 1; // 첫 번째 게시물인 경우
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	close();
        }
        return -1; // 데이터베이스 오류
    }
    public int write(String bbsTitle, String userID, String bbsContent) {
    	getConnection();
    	
        String SQL = "INSERT INTO BBS VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL);
            pstmt.setInt(1, getNext());
            pstmt.setString(2, bbsTitle);
            pstmt.setString(3, userID);
            pstmt.setString(4, getDate());
            pstmt.setString(5, bbsContent);
            pstmt.setInt(6, 1);
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	close();
        }
        return -1; // 데이터베이스 오류
    }
    
    public ArrayList<Bbs> getList(int pageNumber){
    	ArrayList<Bbs> list = new ArrayList<Bbs>();
    	getConnection();
    	try {
    		String SQL = "SELECT * FROM BBS"; // 내림차순으로 가장 마지막에 쓰인 것을 가져온다
    		PreparedStatement pstmt = conn.prepareStatement(SQL);
    		
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
    			Bbs bbs = new Bbs();
    			bbs.setBbsID(rs.getInt(1));
    			bbs.setBbsTitle(rs.getString(2));
    			bbs.setUserID(rs.getString(3));
    			bbs.setBbsDate(rs.getString(4));
    			bbs.setBbsContent(rs.getString(5));
    			bbs.setBbsAvailable(rs.getInt(6));
    			list.add(bbs);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} 
    	
    	return list; 
    }
    
    public boolean nextPage(int pageNumber) {
    	getConnection();
    	String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvailable = 1"; 
    	try {
    		PreparedStatement pstmt = conn.prepareStatement(SQL);
    		pstmt.setInt(1, getNext() - (pageNumber - 1 ) * 10);
    		rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			return true;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		close();
    	}
    	return false; 
    }
    
    public Bbs getBbs(int bbsID){
    		getConnection();
            String SQL = "SELECT * FROM BBS WHERE bbsID = ?"; 
            try {
                PreparedStatement pstmt = conn.prepareStatement(SQL);
                pstmt.setInt(1, bbsID);
                rs = pstmt.executeQuery();
                if (rs.next())
                {
                    Bbs bbs = new Bbs();
                    bbs.setBbsID(rs.getInt(1));
                    bbs.setBbsTitle(rs.getString(2));
                    bbs.setUserID(rs.getString(3));
                    bbs.setBbsDate(rs.getString(4));
                    bbs.setBbsContent(rs.getString(5));
                    bbs.setBbsAvailable(rs.getInt(6));
                    return bbs;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	close();
            }
            return null; 
        }
    
    
    
    
}
  
