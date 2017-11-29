import com.restfb.*;
import com.restfb.Connection;
import com.restfb.types.Comment;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;
import com.restfb.types.send.IdMessageRecipient;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.lang.reflect.Executable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String args[])
    {
        Main mobj = new Main();
        ArrayList<String> notCompleted, notRated;
        String url, pageAccessToken, userAccessToken, name, roomno, rollno, dept, sfbId, efbId, postfbId, description;
        Timestamp sqlDate,date;
        int i;
        pageAccessToken = "EAACEdEose0cBANJYjdLY3Fo6y4HBhohxEkhp9BZAQGtS8gl3KdkKKJ0XtgXZCHIynZA3n1ZC4z3nqTf38fEnG9O3FkGxR9prLCyojQ9D6qWCOG3dp7CYNczcmgtMyhUqaZA5UOUawCZCndQuG95eTpFm6zyQNIWQlVJ6k0ROo244CoZCQNJgvkB8Uv7CI21DZBLZBJlIgIZALZC3gZDZD";
        userAccessToken = "EAACEdEose0cBAAUnDZAHqmNKfh1SmIhRalckrb4EWR8ZAj1hippqVikQZA3BPjdTkwcO2ZBdnsDmC5IHS0Vu1Wqt1cUcOetyyJw2SEms4SQccAgBvPE4ELhAbcow7kVUmnDDVlcP20ZBtwZBYIcZAcbe0OFVqZAa4bZAl3b4mtCzZBNGOxm8HSnZA6dJmDXE8UtyNwZD";
        url = "https://www.facebook.com/FMSiiitdelhi/";

        FacebookClient fbClient = new DefaultFacebookClient(userAccessToken);

        Page myPage = fbClient.fetchObject(url, Page.class);

        //Printing name of the page
        System.out.println(myPage.getName());
        while (true)
        {
            mobj.employeeCheck();

            //Checking whether task has completed or not
            notCompleted = mobj.getNotCompleted();
            for (int j = 0; j < notCompleted.size(); j++)
            {
                if(mobj.checkTime(notCompleted.get(j))==false)
                {
                    System.out.println("fb.com/"+notCompleted.get(j)+" this complaint wasn't completed within 24 hrs.");
                    continue;
                }
                efbId = mobj.getEFbId(notCompleted.get(j));
                Connection<Comment> postComments= fbClient.fetchConnection(notCompleted.get(j)+"/comments", Comment.class);
                for (Comment comnt : postComments.getData())
                {
                    Comment userPost = fbClient.fetchObject(comnt.getId(), Comment.class,
                            Parameter.with("fields", "from"));
                    if(comnt.getMessage().equals("Done.") && userPost.getFrom().getId().equals(efbId))
                    {
                        mobj.postComment(pageAccessToken, notCompleted.get(j), "Your task was completed by the assigned user. Let us know know on a scale of 1-5 how much satisfied you are with the service. Thank you.");
                        mobj.setCompleted(notCompleted.get(j), new Timestamp(comnt.getCreatedTime().getTime()));
                        break;
                    }
                }
            }



            //Checking whether user has provided rating or not
            notRated = mobj.getNotRated();
            for (int j = 0; j < notRated.size(); j++)
            {
                sfbId = mobj.getSFbId(notRated.get(j));
                Connection<Comment> postComments= fbClient.fetchConnection(notRated.get(j)+"/comments", Comment.class);
                for (Comment comnt : postComments.getData())
                {
                    Comment userPost = fbClient.fetchObject(comnt.getId(), Comment.class,
                            Parameter.with("fields", "from"));
                    if((comnt.getMessage().equals("1") ||
                            comnt.getMessage().equals("2") ||
                            comnt.getMessage().equals("3") ||
                            comnt.getMessage().equals("4") ||
                            comnt.getMessage().equals("5"))
                            && userPost.getFrom().getId().equals(sfbId))
                    {
                        mobj.postComment(pageAccessToken, notRated.get(j), "Thank you for your valuable feedback.");
                        mobj.setRated(notRated.get(j), comnt.getMessage().charAt(0)-'0');
                        break;
                    }
                }
            }



            //No of assigned task difference




            //Fetching top 100 posts
            date = mobj.getTime();
            mobj.setTime(new Timestamp(new java.util.Date().getTime()));

            Connection<Post> getPosts = fbClient.fetchConnection(myPage.getId() + "/visitor_posts", Post.class);

            //Get all the details of the posts
            for (Post feed : getPosts.getData())
            {
                sqlDate = new Timestamp(feed.getCreatedTime().getTime());
                if ((sqlDate.getTime() - date.getTime()) < 0)
                    break;
                Post userPost = fbClient.fetchObject(feed.getId(), Post.class,
                        Parameter.with("fields", "from"));

                //Spliting space separated data
                String[] message = feed.getMessage().split(" ");
                System.out.println(feed.getMessage());

                //Registration
                if (message[0].equalsIgnoreCase("REG")) {

                    //Student Registration
                    if (message[1].equalsIgnoreCase("STU")) {
                        rollno = message[2];
                        roomno = message[3];
                        name = userPost.getFrom().getName();
                        sfbId = userPost.getFrom().getId();
                        mobj.registerStudent(rollno, roomno, name, sfbId);
                        mobj.postComment(pageAccessToken, userPost.getId(), "Student has been successfully registered to the system. Thank you!");
                    }

                    //Employee Registration
                    else if (message[1].equalsIgnoreCase("EMP")) {
                        dept = message[2];
                        name = userPost.getFrom().getName();
                        efbId = userPost.getFrom().getId();
                        mobj.registerEmployee(dept, name, efbId);
                        mobj.postComment(pageAccessToken, userPost.getId(), "Employee has been successfully registered to the system. Thank you!");
                    }

                    //Not applicable
                    else {
                        mobj.postComment(pageAccessToken, userPost.getId(), "To register you have to be either student or employee! Thank you!");
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
                    if(mobj.studentExists(sfbId)==Boolean.FALSE)
                    {
                        mobj.postComment(pageAccessToken, postfbId, "Student must be registered to the system in order to make a complaint request.");
                        continue;
                    }
                    for (i = 3; i < message.length - 1; i++)
                        description += (message[i] + " ");
                    description += message[i];
                    efbId = mobj.assignTask(message, sfbId, postfbId, pageAccessToken);
                    if (efbId.equals("")) {
                        mobj.postComment(pageAccessToken, userPost.getId(), "Sorry! No employee found of " + dept + " department! Please try again after sometime. Sorry for the inconvenience caused.");
                    } else {
                        mobj.registerComplaint(mobj, roomno, dept, sfbId, postfbId, description, sqlDate, efbId);
                        mobj.postComment(pageAccessToken, userPost.getId(), "Your complaint has been registered! Your task has been assigned to the employee fb.com/" + efbId + ". Assigned employee will get in touch with you. Thank you.");
                    }
                }

                //Invalid data
                else {
                    mobj.postComment(pageAccessToken, userPost.getId(), "Posts are only for registration and complaint purposes! Please follow exact format! Thank you.");
                    continue;
                }
            }
        }
    }

    void employeeCheck()
    {
        String dept,name;
        int task;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS","root","root");
            String sqlQuery = "select max(tasks) as tasks,dept from employee group by dept;";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);

            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next())
            {
                task = rs.getInt("tasks");
                dept = rs.getString("dept");
                System.out.println(dept+" "+task);
                sqlQuery = "SELECT * from employee where (? - tasks) >10 and dept=?";
                preparedStmnt = con.prepareStatement(sqlQuery);
                preparedStmnt.setInt(1,task);
                preparedStmnt.setString(2,dept);
                ResultSet rs1 = preparedStmnt.executeQuery();
                while (rs1.next())
                {
                    name = rs1.getString("name");
                    System.out.println(name+" of department "+dept+" please report to the admin");
                }
            }

            con.close();
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    Boolean studentExists(String sid)
    {
        Boolean flag = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "select * from student where sfbid = ?";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setString(1, sid);

            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next()) {
                flag = Boolean.TRUE;
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return flag;
    }

    String getEFbId(String postFbId)
    {
        String efbId = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "select * from complaint where postid = ?";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setString(1, postFbId);

            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next())
            {
                efbId = rs.getString("efbid");
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return efbId;
    }


    Boolean checkTime(String postFbID)
    {
        Timestamp stime = new Timestamp(new java.util.Date().getTime());
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "select * from complaint where postid = ?";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setString(1, postFbID);

            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next())
            {
                stime = rs.getTimestamp("stime");
            }
            if(stime.getTime()-new Timestamp(new java.util.Date().getTime()).getTime()>0)
            {
                sqlQuery = "UPDATE complaint SET iscompleted = -1 WHERE postid = ?";
                preparedStmnt = con.prepareStatement(sqlQuery);
                preparedStmnt.setString(1,postFbID);
                preparedStmnt.execute();
                return false;
            }
            return true;
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return true;
    }


    String getSFbId(String postFbId)
    {
        String sfbId = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "select * from complaint where postid = ?";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setString(1, postFbId);

            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next())
            {
                sfbId = rs.getString("sfbid");
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return sfbId;
    }

    void setRated(String id, int rating)
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "UPDATE complaint SET israted = 1, rating = ? WHERE postid = ?";

            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setInt(1, rating);
            preparedStmnt.setString(2, id);
            preparedStmnt.execute();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    void setCompleted(String id, Timestamp time)
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "UPDATE complaint SET iscompleted = 1, etime = ? WHERE postid = ?";

            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setTimestamp(1, time);
            preparedStmnt.setString(2, id);
            preparedStmnt.execute();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }


    ArrayList<String> getNotCompleted()
    {
        ArrayList<String> temp = new ArrayList<String>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "select * from complaint where iscompleted = 0";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);

            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next())
            {
                temp.add(rs.getString("postid"));
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return temp;
    }


    ArrayList<String> getNotRated()
    {
        ArrayList<String> temp = new ArrayList<String>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "select * from complaint where israted = 0";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);

            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next())
            {
                temp.add(rs.getString("postid"));
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return temp;
    }

    Timestamp getTime()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "select * from timestamp where id = 1";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next())
            {
                return rs.getTimestamp("lasttime");
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return (new Timestamp(System.currentTimeMillis()));
    }

    void setTime(Timestamp date)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS", "root", "root");
            String sqlQuery = "UPDATE timestamp SET lasttime = ? WHERE id = 1";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setTimestamp(1, date);
            preparedStmnt.execute();
            con.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    String assignTask(String[] messages, String sFbId, String posfbId, String pageAccessToken)
    {
        String efbId="";
        int noTask = 1000000000,tTask;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/FMS","root","root");
            String sqlQuery = "select * from employee where dept = ?";
            PreparedStatement preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setString(1, messages[2]);

            ResultSet rs = preparedStmnt.executeQuery();
            while (rs.next())
            {
                tTask = rs.getInt("tasks");
                if(tTask <= noTask)
                {
                    noTask = tTask;
                    efbId = rs.getString("efbid");
                }
            }


            //Update complaint table with the assigned employee ID
            sqlQuery = "UPDATE complaint SET efbid = ? WHERE postid = ?";
            preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setString(1, efbId);
            preparedStmnt.setString(2, posfbId);
            preparedStmnt.execute();


            //Update employee table with the assigned employee task count
            sqlQuery = "UPDATE employee SET tasks = ? WHERE efbid = ?";
            preparedStmnt = con.prepareStatement(sqlQuery);
            preparedStmnt.setInt(1, noTask+1);
            preparedStmnt.setString(2, efbId);
            preparedStmnt.execute();

            con.close();
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
        return efbId;
    }

    void  postComment(String accessToken, String id, String message)
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


    void registerComplaint(Main mobj, String roomno, String dept, String sfbId, String postFbId, String desc, Timestamp date, String efbId)
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
            preparedStmt.setString(6, efbId);
            preparedStmt.setTimestamp(7, date);

            preparedStmt.execute();
            con.close();
        }

        catch(Exception e)
        {
            System.out.println(e);
        }
    }

}
