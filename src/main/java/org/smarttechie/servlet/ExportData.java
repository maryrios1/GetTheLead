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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

/**
 *
 * @author mary
 */
public class ExportData extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        JSONObject obj = new JSONObject();
        try{
        Cluster cluster;
        Session session;
        cluster = Cluster.builder().addContactPoint("localhost").build();
        session = cluster.connect("GetTheLead");
        /*
        String statement= "COPY TweetsTest " +
                        "(Id, UserId,User, CurrentUserRetweetId, " +
                        "InReplyToUserId,ProfileLocation,Message,CreatedAt,IsRetweet,IsRetweeted,"  +
                        "IsFavorited,QuotedStatusId,Contributors,InReplyToScreenName,RetweetCount,"  +
                        "GeoLocationLatitude,GeoLocationLongitude,HashtagEntities,PlaceCountry," +
                        "PlaceFullName,Source,IsPossiblySensitive, IsTruncated) " +
                        "TO '/home/mary/Documents/Tesis/temp.csv' ";
        
        executeQuery(session,statement);*/
        String cassandraHome = "sh /root/cassandra/";
        String hostname = "localhost";
        String rpcPort = "9042";
        String exportCommand = cassandraHome + "bin/cqlsh " + hostname + " " + rpcPort + " -f /home/mary/Documents/Tesis/exportData1.txt"; // file which holds export command

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(exportCommand);
        
        InputStream isError = process.getErrorStream();
        InputStreamReader isrError = new InputStreamReader(isError);
        BufferedReader brError = new BufferedReader(isrError);
        
        String lineError = null;

        while ((lineError = brError.readLine()) != null)
        {
            System.out.printf(lineError);
        }
        
        // for keep tracking the log, you can do following. 
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;

        while ((line = br.readLine()) != null)
        {
            System.out.printf(line);
        }
        obj.put("success", true);
        obj.put("message", "success");
        }
        catch (Exception e){
           obj.put("success", false); 
           obj.put("message", e.getMessage().toString()); 
        }
        finally{
            response.getWriter( ).write(obj.toJSONString());
        }
        
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
        return "Short description";
    }// </editor-fold>

}
