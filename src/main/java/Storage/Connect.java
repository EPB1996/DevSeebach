package Storage;


import org.glassfish.grizzly.utils.Pair;

import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sqlitetutorial.net
 */
public class Connect {
    /**
     * Connect to a sample database
     */


    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:/home/epb1996/Project/fotoseebach/tests.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    public Pair<String,String> getOwnerIp(String owner){
        String sql = "SELECT OwnerName,OwnerIp FROM OwnerIp WHERE OwnerId = " + owner;
        Pair<String,String> res = new Pair<>();

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {


             res.setFirst(rs.getString("OwnerName"));
             res.setSecond(rs.getString("OwnerIp"));


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;

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
            pstmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Remove User from groupAssociationList
     *
     * @param groupid id of group
     * @param userid  id of user
     */
    public void deleteMemberFromGroup(long groupid, String userid) {
        String sql = "DELETE FROM UserGroup WHERE (GroupId,UserId) = (?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, groupid);
            pstmt.setString(2, userid);


            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    /**
     *Add member to group
     * @param groupid id of group
     * @param userid  id of user
     */
    public void addMemberToGroup(long groupid, String userid){
        String sql = "INSERT INTO UserGroup(GroupId,UserId, UserName, MemberSince,PostNumber) VALUES(?,?,?,?,0)";
        int realId = Integer.parseInt(userid.split("!")[1]);
        String realName = userid.split("!")[0];

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, (int) groupid);
            pstmt.setInt(2, realId);
            pstmt.setString(3, realName);
            pstmt.setDate(4, new java.sql.Date(Calendar.getInstance().getTime().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get all associated groups of chatID
     *
     * @param chatId ChatId of the uploader
     *        sendTo fotowall/grouid to send to
     *
     *
     */
    public void updateUserPosts(long chatId,String sendTo) {

        String sql = "UPDATE UserGroup SET PostNumber = Postnumber +1 WHERE UserId = "+chatId +
                " And GroupId = " + sendTo;
        String sql1 = "UPDATE RegisteredUser SET PostNumber = Postnumber +1 WHERE UserId = "+chatId;



        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
             ) {

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        try (Connection conn = this.connect();
               Statement stmt = conn.createStatement();
               ResultSet rs = stmt.executeQuery(sql1);
        ) {

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }



    }

    /**
     * create new group which has access to fotowall
     *
     * @param ownerId :fotowall owner who started this group
     */
    public boolean insertNewGroup(int ownerId,String ownerName) {
        String sql = "INSERT INTO [Group](GroupOwner,GroupName,Privat) VALUES(?,?,0)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setInt(1, ownerId);
             pstmt.setString(2, ownerName);

             pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Get all associated groups of chatID
     *
     * @param ownerId ChatId of the owner
     *                <p>
     *                returns groups
     * @return
     */
    public int getGroupId(int ownerId) {

        int groupId = 0;
        String sql = "SELECT GroupId FROM [Group] WHERE GroupOwner = " + ownerId;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {


            groupId = rs.getInt("GroupId");


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return groupId;
    }

    /**
     * Get all associated groups of chatID
     *
     * @param ownerId ChatId of the owner
     *                <p>
     *                returns groups
     * @return
     */
    public HashMap<String,String> getAssociatedGroups(long ownerId) {

        HashMap<String,String> res = new HashMap<>();

        String sql = "SELECT GroupOwner,GroupName\n" +
                "FROM [Group],UserGroup \n" +
                "WHERE UserGroup.UserId = "+ ownerId+ "\n" +
                "AND UserGroup.GroupId = [Group].GroupOwner" ;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
            res.put(rs.getString("GroupOwner"),rs.getString("GroupName"));

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;
    }


    /**
     * Get All registered User which are not already added to you
     * @param   chatId  to get memberlist of group associated with chatid
     */
    public HashMap<String,String> getAvailableUsers(long chatId){
        HashMap<String, String> res = new HashMap<>();

        String sql = "SELECT userId,UserName\n" +
                "FROM RegisteredUser\n" +
                "EXCEPT\n" +
                "SELECT userId,UserName\n" +
                "FROM UserGroup\n" +
                "WHERE GroupId = "+chatId;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                res.put(String.valueOf(rs.getInt("UserId")), rs.getString("UserName"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;
    }


    /**
     * Get Memberlist of owener of a fotowall
     *
     * @param ownerId
     */
    public HashMap<String, String> getMemberlist(long ownerId) {
        HashMap<String, String> res = new HashMap<>();
        String sql = "SELECT UserId,UserName FROM [UserGroup] WHERE GroupId = " + ownerId;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                res.put(String.valueOf(rs.getInt("UserId")), rs.getString("UserName"));
            }
            System.out.println(res.get(rs.getLong("UserId")));


        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;

    }

    /**
     * Get user associated with group
     *
     * @param groupid id of group
     * @param userid  id of user
     */
    public HashMap<String, String> getGroupMember(long groupid, Integer userid) {
        HashMap<String, String> res = new HashMap<>();
        String sql = "SELECT UserName,MemberSince,PostNumber FROM (SELECT * FROM UserGroup WHERE UserId = "
                + userid + ") WHERE GroupId = " + groupid;

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                res.put("UserName", rs.getString("UserName"));
                res.put("MemberSince", String.valueOf(rs.getDate("MemberSince")));
                res.put("PostNumber", String.valueOf(rs.getInt("PostNumber")));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;

    }

    public Set<String[]> getFullMemberInformation(long ownerId){
        Set<String[]> res = new HashSet<>();
        String sql = "SELECT UserName,MemberSince,PostNumber FROM [UserGroup] WHERE GroupId = " + ownerId;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                res.add(new String[]{rs.getString("UserName"),String.valueOf(rs.getDate("MemberSince")),
                String.valueOf(rs.getInt("PostNumber"))});
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;

    }

    public Set<String[]> getFullUserInformation(long chatId){
        Set<String[]> res = new HashSet<>();
        String sql = "SELECT UserName,RegisteredSince,PostNumber FROM [RegisteredUser] WHERE UserId = " + chatId;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                res.add(new String[]{rs.getString("UserName"),String.valueOf(rs.getDate("RegisteredSince")),
                        String.valueOf(rs.getInt("PostNumber"))});
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;

    }

    /**
     * Gets Set of owners
     * @return Set<Long> of owners
     */
    public Set<Integer> getOwnerSet(){
        Set<Integer> res = new HashSet<>();
        String sql = "SELECT DISTINCT GroupId FROM [UserGroup]" ;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                res.add(rs.getInt("GroupId"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;

    }

    /**
     * Get set groupname and groupid
     * @return Set<Pair<long,string>>
     */
    public HashMap<String,String> getOwnerGroupPairs(long chatId){
        HashMap<String,String> res = new HashMap<>();
        String sql = "SELECT GroupOwner,GroupName\n" +
                "FROM [Group]\n" +
                "WHERE Privat = 0 \n" +
                "EXCEPT\n" +
                "SELECT GroupOwner,GroupName\n" +
                "FROM [Group],UserGroup\n" +
                "WHERE UserGroup.UserId = "+chatId+"\n" +
                "AND UserGroup.GroupId = [Group].GroupOwner";


        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                res.put(rs.getString("GroupOwner"),rs.getString("GroupName"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return res;

    }






}
