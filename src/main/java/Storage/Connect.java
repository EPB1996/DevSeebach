package Storage;

import java.sql.*;

/**
 *
 * @author sqlitetutorial.net
 */
public class Connect {
    /**
     * Connect to a sample database
     */



    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://Users/EPB-Surface/IdeaProjects/DevSeebach/tests.db";
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
     * @param UserId
     * @param UserName
     */
    public void insertUnregeisteredUser(int UserId, String UserName) {
        String sql = "INSERT INTO RegisteredUser(UserId, UserName, RegisteredSince) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, UserId);
            pstmt.setString(2, UserName);
            pstmt.setInt(3,1);
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

}
