/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.smarttechie.servlet;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AlreadyExistsException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryTimeoutException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.exceptions.SyntaxError;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
//import org.neo4j.io.fs.FileUtils;
//import src.classes.EmbeddedNeo4j;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
/**
 *
 * @author mary
 */
public class SimpleStream extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    GraphDatabaseService graphDb;
    Node firstNode;
    Node secondNode;
    Relationship relationship;
    private static final String DB_PATH = "/home/mary/Codes/GetTheLeadMaven/neo/tweet-db";//"target/neo4j-hello-db";
    public String greeting;
    TwitterStream twitterStream;
    StatusListener listener;
    TwitterStreamFactory streamFactory;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keywords = request.getParameter("keywords");
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("Y8NvcZqWjUvNR0wfict2rSKmx");
        cb.setOAuthConsumerSecret("A3K8YqVjLpTN5sSbk9MJ8DmiwIxRapLmyhmZcCau55sqzPjA1y");
        cb.setOAuthAccessToken("566064066-BMF8JBt2JI7c4KBWEDtxRqPN2rLNxwKcUoykzoTR");
        cb.setOAuthAccessTokenSecret("wo4LnwlsYYfbYkGixN0CS3NxlYfXxbxwl0gWfpQTIKas4");
        //PrintStream out = new PrintStream(new FileOutputStream("/home/mary/Documents/Tesis/output.txt"));
        //System.setOut(out);
        Cluster cluster;
        Session session;
        cluster = Cluster.builder().addContactPoint("localhost").build();
        session = cluster.connect("GetTheLead");
        String[] parametros = {"TuiteraMx",keywords};
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        //EmbeddedNeo4j neo = new EmbeddedNeo4j();
        //neo.createDb();
        //neo.removeData();
        //neo.shutDown();
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        registerShutdownHook( graphDb );
        getStream(twitterStream,parametros,session);//,out);
        //out.close();
        
    }
    
    public void stopStream(){

        twitterStream.shutdown();
        //m_logger.info("shutdown done");
        twitterStream = null;
        listener = null;
        shutDown();
    }
    
    public void getStream(TwitterStream twitterStream, String[] parametros,
            final Session session)//,PrintStream out)
    {
        
        listener = new StatusListener() {

            @Override
            public void onException(Exception arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrubGeo(long arg0, long arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStatus(Status status) {
                Twitter twitter = new TwitterFactory().getInstance();
                
                
                User user = status.getUser();
                
                // gets Username
                String username = status.getUser().getScreenName();
                System.out.println("");
                String profileLocation = user.getLocation();
                System.out.println(profileLocation);
                long tweetId = status.getId(); 
                System.out.println(tweetId);
                String content = status.getText();
                System.out.println(content +"\n");
                
                JSONObject obj = new JSONObject();
                obj.put("User", status.getUser().getScreenName());
                obj.put("ProfileLocation", user.getLocation().replaceAll("'", "''"));
                obj.put("Id", status.getId());                
                obj.put("UserId", status.getUser().getId());
                //obj.put("User", status.getUser());
                obj.put("Message", status.getText().replaceAll("'", "''"));
                obj.put("CreatedAt", status.getCreatedAt().toString());
                obj.put("CurrentUserRetweetId", status.getCurrentUserRetweetId());
                //Get user retweeteed
                String otheruser;
                try{
                    if(status.getCurrentUserRetweetId()!=-1){
                        User user2 = twitter.showUser(status.getCurrentUserRetweetId());
                        otheruser = user2.getScreenName();
                        System.out.println("Other user: " + otheruser);
                    }
                }
                catch(Exception ex)
                {
                    System.out.println("ERROR: " +  ex.getMessage().toString());
                }
                obj.put("IsRetweet", status.isRetweet());
                obj.put("IsRetweeted", status.isRetweeted());
                obj.put("IsFavorited", status.isFavorited());
                
                obj.put("InReplyToUserId", status.getInReplyToUserId());
                //In reply to
                obj.put("InReplyToScreenName",status.getInReplyToScreenName());
                
                obj.put("RetweetCount",status.getRetweetCount());
                if(status.getGeoLocation()!=null){
                    obj.put("GeoLocationLatitude",status.getGeoLocation().getLatitude());
                    obj.put("GeoLocationLongitude",status.getGeoLocation().getLongitude());
                }
                
                JSONArray listHashtags = new JSONArray();
                String hashtags="";
                for(HashtagEntity entity: status.getHashtagEntities()){
                    listHashtags.add(entity.getText());                
                    hashtags +=entity.getText() + ",";
                }
                
                if(!hashtags.isEmpty())
                    obj.put("HashtagEntities", hashtags.substring(0,hashtags.length()-1));
                
                if(status.getPlace()!=null){
                    obj.put("PlaceCountry", status.getPlace().getCountry());
                    obj.put("PlaceFullName", status.getPlace().getFullName());
                }
                
                obj.put("Source", status.getSource());
                obj.put("IsPossiblySensitive", status.isPossiblySensitive());
                obj.put("IsTruncated", status.isTruncated());
                
                if(status.getScopes()!=null){
                    JSONArray listScopes = new JSONArray();
                    String scopes="";
                    for(String scope: status.getScopes().getPlaceIds()){
                        listScopes.add(scope);
                        scopes += scope +",";
                    }
                    
                    if(!scopes.isEmpty())
                        obj.put("Scopes", scopes.substring(0, scopes.length()-1));
                }
                
                obj.put("QuotedStatusId", status.getQuotedStatusId());                

                JSONArray list = new JSONArray();
                String contributors="";
                for(long id: status.getContributors()){
                    list.add(id);
                    contributors+=id +",";
                }
                
                if(!contributors.isEmpty())
                    obj.put("Contributors", contributors.substring(0, contributors.length()-1));
                
                System.out.println("" + obj.toJSONString());
                
                insertNodeNeo4j(obj);
                
                //out.println(obj.toJSONString());
                String statement = "INSERT INTO TweetsTest JSON '" + obj.toJSONString() + "';";
                executeQuery(session,statement);
            }

            @Override
            public void onTrackLimitationNotice(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStallWarning(StallWarning sw) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            

        };
        FilterQuery fq = new FilterQuery();        

        fq.track(parametros);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);
    }
    
    private void insertNodeNeo4j(JSONObject obj) {
        //retweeted
        
        // START SNIPPET: transaction
        try (Transaction tx = graphDb.beginTx()) {
            // Database operations go here
            // END SNIPPET: transaction
            // START SNIPPET: addData

            firstNode = graphDb.createNode();
            firstNode.setProperty("name", obj.get("User"));
            firstNode.setProperty("id", obj.get("UserId"));
            if(obj.get("InReplyToScreenName")!= null)
            {
                secondNode = graphDb.createNode();
                secondNode.setProperty("name", obj.get("InReplyToScreenName"));
                secondNode.setProperty("id", obj.get("InReplyToUserId"));
                relationship = firstNode.createRelationshipTo(secondNode, RelTypes.KNOWS);
                relationship.setProperty("relation", "replied");
            }
            // END SNIPPET: addData           

            // START SNIPPET: transaction
            tx.success();
        }
        // END SNIPPET: transaction
    }

private ResultSet executeQuery(Session session,String statement){
    ResultSet result=null;
    try {
        result=session.execute(statement);
    }
    catch (  AlreadyExistsException e) {
        System.out.println("Keyspace or table already exists");
    }
    catch (  NoHostAvailableException e) {
        System.out.printf("No host in the %s cluster can be contacted to execute the query.\n",session.getCluster());
        e.printStackTrace();
    }
    catch (  QueryTimeoutException e) {
        System.out.println("An exception has been thrown by Cassandra because the query execution has timed out.");
        e.printStackTrace();
    }
    catch (  QueryExecutionException e) {
        System.out.println("An exception was thrown by Cassandra because it cannot " + "successfully execute the query with the specified consistency level.");
        e.printStackTrace();
    }
    catch (  SyntaxError e) {
        System.out.printf("The query '%s' has a syntax error.\n message=%s",statement,e.getMessage());
        e.printStackTrace();
    }
    catch (  QueryValidationException e) {
        System.out.printf("The query '%s' is not valid, for example, incorrect syntax.\n",statement);
        e.printStackTrace();
    }
    catch (  IllegalStateException e) {
        System.out.println("The BoundStatement is not ready.");
        e.printStackTrace();
    }
    
    return result;
}

private static enum RelTypes implements RelationshipType
{
    KNOWS
}

// START SNIPPET: shutdownHook
private static void registerShutdownHook(final GraphDatabaseService graphDb) {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running application).
    Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
            graphDb.shutdown();
        }
    });
}
    // END SNIPPET: shutdownHook
    public void removeData() {
        try (Transaction tx = graphDb.beginTx()) {
            // START SNIPPET: removingData
            // let's remove the data
            firstNode.getSingleRelationship(RelTypes.KNOWS, Direction.OUTGOING).delete();
            firstNode.delete();
            secondNode.delete();
            // END SNIPPET: removingData
            tx.success();
        }
    }

    public void shutDown() {
        System.out.println();
        System.out.println("Shutting down database ...");
        // START SNIPPET: shutdownServer
        graphDb.shutdown();
        // END SNIPPET: shutdownServer
    }
    // START SNIPPET: shutdownHook
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Get the current tweets related to the topic";
    }// </editor-fold>

}