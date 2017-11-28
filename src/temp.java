//        User user = fbClient.fetchObject("me", User.class);
//        Connection<User> myFriends = fbClient.fetchConnection(user.getId()+"/friends", User.class);
//        System.out.println(myFriends.getTotalCount());
//        for (User friend: myFriends.getData())
//        {
//            System.out.println(friend.getId());
//
//            Connection<Page> myMovies = fbClient.fetchConnection(friend.getId()+"/movies", Page.class);
//            String content = "Name:" + friend.getName();
//            for (Page movies: myMovies.getData())
//            {
//                content = content + "\n" + "Movies: "+movies.getName()+"\n";
//            }
//            System.out.println(content);
//
//        }





import java.sql.*;
class MysqlCon
{
    public static void main(String args[])
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS","root","root");
                    //here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from employee");
            while(rs.next())
                System.out.println(rs.getString(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
            con.close();
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}