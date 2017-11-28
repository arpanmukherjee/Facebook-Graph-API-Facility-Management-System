import com.restfb.*;
import com.restfb.Connection;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.send.IdMessageRecipient;

import java.sql.*;

public class Main
{
    public static void main(String args[])
    {
        Main mobj = new Main();
        String url, pageAccessToken, userAccessToken, name, roomno, rollno, dept, sfbId, efbId, postfbId, description, date;
        int i;
        pageAccessToken = "EAACEdEose0cBAKjpQAcbZCfp9OqujLRFTAEZBVBDAae14z6gRWeMkDI6Hb6afmLbCpn1LZAeTIXOuUZB8OStbtUmjZCRuAPfKDdZBLSttXbFZCAZA5S6RPHoLRiuG2b2WP7UBHZC4QmrREipW3U5ea7yyQUcx91lCu157wngYAAWSvKjxHwBHZByy8HvZAmWTYAo3RLXVZALcxkdAgZDZD";
        userAccessToken = "EAACEdEose0cBAGOiashCxqDEwt390Y1bad4KTbW1NnPvKNogourusX5yplWmDdY9wq6JkOmLsdxGZAZBl3mEbMXBQlBlHhkZBHQIcOvl0EfXNZBAZCZCLSnuhRKwQ8VRw41ZAg7OEHjtKCKFt99vTD0Bm2m6tjnVpy3H0ipLZAGb8Uz5QmrR3HCCISw8ShMT0T8ZD";
        url = "https://www.facebook.com/FMSiiitdelhi/";

        FacebookClient fbClient = new DefaultFacebookClient(userAccessToken);

        Page myPage = fbClient.fetchObject(url, Page.class);


        //Printing name of the page
        System.out.println(myPage.getName());

        //Fetching top 100 posts
        Connection<Post> getPosts = fbClient.fetchConnection(myPage.getId()+"/visitor_posts",
                Post.class,
                Parameter.with("limit", 100));

        //Get all the details of the posts
        for (Post feed : getPosts.getData())
        {
            Post userPost = fbClient.fetchObject(feed.getId(), Post.class,
                    Parameter.with("fields", "from"));

            //Spliting space separated data
            String[] message = feed.getMessage().split(" ");

            //Registration
            if (message[0].equalsIgnoreCase("REG"))
            {

                //Student Registration
                if(message[1].equalsIgnoreCase("STU"))
                {
                    rollno = message[2];
                    roomno = message[3];
                    name = userPost.getFrom().getName();
                    sfbId = userPost.getFrom().getId();
                    mobj.registerStudent(rollno, roomno, name, sfbId);
                    mobj.postComment(pageAccessToken, userPost.getId(), "Student has been successfully registered to the system. Thank you!");
                }

                //Employee Registration
                else if(message[1].equalsIgnoreCase("EMP"))
                {
                    dept = message[2];
                    name = userPost.getFrom().getName();
                    efbId = userPost.getFrom().getId();
                    mobj.registerEmployee(dept, name, efbId);
                    mobj.postComment(pageAccessToken, userPost.getId(), "Employee has been successfully registered to the system. Thank you!");
                }

                //Not applicable
                else
                {
                    mobj.postComment(pageAccessToken, userPost.getId(),"To register you have to be either student or employee! Thank you!");
                    continue;
                }
            }

            //Complaint
            else if (message[0].equalsIgnoreCase("COMP"))
            {
                description = "";
                roomno = message[1];
                dept = message[2];
                sfbId = userPost.getFrom().getId();
                postfbId = userPost.getId();
                for (i = 3; i < message.length-1; i++)
                    description += (message[i]+" ");
                description += message[i];
                java.sql.Date sqlDate = new java.sql.Date(feed.getCreatedTime().getTime());
                mobj.registerComplaint(roomno, dept, sfbId, postfbId, description, sqlDate);
                mobj.postComment(pageAccessToken, userPost.getId(), "Your complaint has been registered to our system! We will inbox you updates!");
            }

            //Invalid data
            else
            {
                mobj.postComment(pageAccessToken, userPost.getId(), "Posts are only for registration and complaint purposes! Please follow exact format! Thank you!");
                continue;
            }
        }
    }

    String assignTask()
    {
        
    }

    void postComment(String accessToken, String id, String message)
    {
        DefaultFacebookClient client =  new DefaultFacebookClient(accessToken);
        client.publish(id+"/comments", String.class, Parameter.with("message", message));
    }


    void registerStudent(String rollno, String roomno, String name, String fbId)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS","root","root");
            String query = "INSERT INTO student (rollno, name, roomno, sfbid)"+"VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, rollno);
            preparedStmt.setString(2, name);
            preparedStmt.setString(3, roomno);
            preparedStmt.setString(4, fbId);

            preparedStmt.execute();
            con.close();
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
    }


    void registerEmployee(String dept, String name, String efbId)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS","root","root");
            String query = "INSERT INTO employee (efbid, name, dept, tasks)"+"VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, efbId);
            preparedStmt.setString(2, name);
            preparedStmt.setString(3, dept);
            preparedStmt.setInt(4, 0);

            preparedStmt.execute();
            con.close();
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    void registerComplaint(String roomno, String dept, String sfbId, String postFbId, String desc, Date date)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS","root","root");
            String query = "INSERT INTO complaint (postid, sfbid, roomno, dept, descr, efbid, stime)"+"VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString(1, postFbId);
            preparedStmt.setString(2, sfbId);
            preparedStmt.setString(3, roomno);
            preparedStmt.setString(4, dept);
            preparedStmt.setString(5, desc);
            preparedStmt.setString(6, "bla");
            preparedStmt.setDate(7, date);

            preparedStmt.execute();
            con.close();
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
    }

}
