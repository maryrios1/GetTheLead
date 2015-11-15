package org.smarttechie.servlet;


 
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
 
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
 
/**
* Servlet implementation class TwitterSigninServlet
*/
public class TwitterSigninServlet extends HttpServlet {
private static final long serialVersionUID = 1L;
private final String CONSUMER_KEY = "Y8NvcZqWjUvNR0wfict2rSKmx";
private final String CONSUMER_SECRET = "A3K8YqVjLpTN5sSbk9MJ8DmiwIxRapLmyhmZcCau55sqzPjA1y";
private final String ACCESS_TOKEN = "566064066-BMF8JBt2JI7c4KBWEDtxRqPN2rLNxwKcUoykzoTR";
private final String ACCESS_TOKEN_SECRET = "wo4LnwlsYYfbYkGixN0CS3NxlYfXxbxwl0gWfpQTIKas4";
/**
* @see HttpServlet#HttpServlet()
*/
public TwitterSigninServlet() {
 super();
}
 
/**
* @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
*/
protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
{
         try 
         {
              
              Twitter twitter = TwitterFactory.getSingleton();
              
              /*
              
              String[] srch = new String[] {"maryrios182","firefox"};
              ResponseList<User> users = twitter.lookupUsers(srch);
              for (User user : users) {
                System.out.println("Friend's Name " + user.getName()); // this print my friends name
                    if (user.getStatus() != null) 
                    {
                        System.out.println("Friend timeline");
                        List<Status> statusess = twitter.getUserTimeline(user.getName());
                        for (Status status3 : statusess) 
                        {
                                System.out.println(status3.getText());
                        }
                    }
                }
            */
             System.out.println("buscar");
             String[] args = new String[] {"TuiteraMx","Tuitera"};
             getStream(twitter,args);
             /*
             try {
                 
                Query query = new Query("facebookdown");
                QueryResult result;
                    do {
                    result = twitter.search(query);
                    List<Status> tweets = result.getTweets();
                    for (Status tweet : tweets) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                    }
                }while ((query = result.nextQuery()) != null);
                 
                    
                } catch (TwitterException te) {
                    te.printStackTrace();
                    System.out.println("Failed to search tweets: " + te.getMessage());
                    System.exit(-1);
                }
              
            System.out.println("redirigir");
            
            /*RequestToken requestToken;
            requestToken = twitter.getOAuthRequestToken("http://127.0.0.1:8084/GetTheLead/views/redirect.jsp");*/
            
            //String authURL = requestToken.getAuthenticationURL();
            //System.out.println(authURL);
            
            request.getSession().setAttribute("requestToken", twitter);
            response.sendRedirect("http://127.0.0.1:8084/GetTheLead/views/redirect.jsp");
            
        }
        catch (Exception e){
            System.out.println("ERROR: "+ e.getStackTrace().toString()+"\n" + e.getMessage());
        }
       }

    private static boolean isNumericalArgument(String argument) {
        String args[] = argument.split(",");
        boolean isNumericalArgument = true;
        
        for (String arg : args) {
            try {
                Integer.parseInt(arg);
            } catch (NumberFormatException nfe) {
                isNumericalArgument = false;
                break;
            }
        }
        
        return isNumericalArgument;
    }

    private void getStream(Twitter twitter,String args[]) throws FileNotFoundException, UnsupportedEncodingException {
        
        Date date =  new Date();
        /*Cluster cluster;
        Session session;
        cluster = Cluster.builder().addContactPoint("localhost").build();
        session = cluster.connect("GetTheLead");
        */
        System.out.println("get stream");
        //PrintWriter writer = new PrintWriter("/home/mary/Documents/Tesis/StreamTwitter/tweets_" + date.toString() + ".txt", "UTF-8");       
                
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                
                //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
                JSONObject obj = new JSONObject();
                obj.put("Id", status.getId());                
                obj.put("UserId", status.getUser().getId());
                //obj.put("User", status.getUser());
                obj.put("Text", status.getText());
                obj.put("CreatedAt", status.getCreatedAt());
                obj.put("CurrentUserRetweetId", status.getCurrentUserRetweetId());
                obj.put("IsRetweet", status.isRetweet());
                obj.put("IsRetweeted", status.isRetweeted());
                obj.put("IsFavorited", status.isFavorited());
                
                obj.put("InReplyToUserId", status.getInReplyToUserId());
                obj.put("QuotedStatusId", status.getQuotedStatusId());

                JSONArray list = new JSONArray();
                list.add(status.getContributors());
                obj.put("Contributors", list);
                System.out.println("json:" + obj.toJSONString());
                //writer.println(obj.toJSONString());
		//writer.flush();
                /*session.execute("INSERT INTO users (Id, UserId, CurrentUserRetweetId, InReplyToUserId," + 
                        " User) VALUES (" + status.getId() + ", " + 
                        status.getUser().getId() +", " + status.getCurrentUserRetweetId() + 
                        ", " + status.getInReplyToUserId() +", '" + status.getUser() + "')");*/
            }
            
            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }
            
            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }
            
            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }
            
            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }
            
            @Override
            public void onException(Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
                ex.printStackTrace();
            }
        };
        
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);
        /*
        ArrayList<Long> follow = new ArrayList<Long>();
        ArrayList<String> track = new ArrayList<String>();
        for (String arg : args) {
            if (isNumericalArgument(arg)) 
            {
                for (String id : arg.split(",")) 
                {
                    follow.add(Long.parseLong(id));
                }
            } 
            else {
                track.addAll(Arrays.asList(arg.split(",")));
            }
        }
        
        //long[] followArray = new long[follow.size()];
        
        //for (int i = 0; i < follow.size(); i++) {
          //  followArray[i] = follow.get(i);
        //}
        
        //String[] trackArray = track.toArray(new String[track.size()]);
        // filter() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
        //twitterStream.filter(new FilterQuery(0, followArray, trackArray));
        FilterQuery filter = new FilterQuery(trackArray);
        twitterStream.filter(filter);
        */
        ///new
        FilterQuery fq = new FilterQuery();
    
        

        fq.track(args);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);  
        
        //writer.close();
        //cluster.close();
    }
}