package Storage;

import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author sqlitetutorial.net
 */
public class Connect {
    /**
     * Connect to a sample database
     */


    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://Users/EPB/IdeaProjects/fotoseebach/tests.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Insert a new row into the warehouses table
     *
     * @param UserId   USerID
     * @param UserName UserName from Telegram, not actual name
     */
    public void insertUnregeisteredUser(int UserId, String UserName) {
        String sql = "INSERT INTO RegisteredUser(UserId, UserName, RegisteredSince) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, UserId);
            pstmt.setString(2, UserName);
            pstmt.setDate(3, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * create new group which has access to fotowall
     *
     * @param ownerId :fotowall owner who started this group
     *
     */
    public void insertNewGroup(int ownerId) {

        // SQL statement for creating a new table
        String sql = "INSERT INTO [Group](GroupOwner) VALUES(?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);

            // create a new table
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }

    }

    /**
     * Get all associated groups of chatID
     *
     * @param ownerId   ChatId of the owner
     *
     * returns groups
     * @return
     */
    public int getGroupId(int ownerId){

        int groupId = 0;
        String sql = "SELECT GroupId FROM [Group] WHERE GroupOwner = " +ownerId;

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){


                groupId = rs.getInt("GroupId");


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return groupId;
    }


    /**
     * Get Memberlist
     *
     * @param   ownerId
     */
    public HashMap<Long,String> getMemberlist(long ownerId){
        HashMap<Long,String > res = new HashMap<>();
        String sql = "SELECT UserId,UserName FROM [UserGroup] WHERE GroupId = " + ownerId;

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            System.out.println(rs.getInt("UserId"));
            while(rs.next()) {
                res.put((long) rs.getInt("UserId"), rs.getString("UserName"));
            }
            System.out.println(res.get(rs.getLong("UserId")));



        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;

    }
}
