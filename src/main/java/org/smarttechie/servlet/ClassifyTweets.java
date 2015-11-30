/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.smarttechie.servlet;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import org.json.simple.JSONObject;

/**
 *
 * @author mary
 */
public class ClassifyTweets extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    DoccatModel model;
    String TABLE;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
        Cluster cluster;
        final Session session;
        TABLE = request.getParameter("Table");
        TABLE = "Tweetsclassification";
        cluster = Cluster.builder().addContactPoint("localhost").build();
        session = cluster.connect("GetTheLead");
        
        //Tarea serial (no se puede paralelizar)
        trainModel();       
       
        final int cores = Runtime.getRuntime().availableProcessors();

        ExecutorService exec = Executors.newFixedThreadPool(cores);
    
        String query = "SELECT COUNT(*) FROM " + TABLE;
        ResultSet results = session.execute(query);
        Row rowCount = results.one();
        Long numberRows = rowCount.getLong("count");
        int numberRowsInt = (int) (long) numberRows;
        final int numberRowsByCore = numberRowsInt/cores + 1;
                        
        results = session.execute("SELECT * FROM GetTheLead." + TABLE );
        //Proceso en paralelo
        // Get current time        
        final long start = System.currentTimeMillis();
        final List <Row> lRows = results.all();        
          
        for (int i=1; i<cores+1; i++) {
            exec.execute(new Runnable() {
              public void run() {
                long threadId =  Thread.currentThread().getId()%cores +1;
                System.out.println("I am thread " + threadId + " of " + cores);
                int startLimit = numberRowsByCore*(int)threadId - numberRowsByCore;
                int endLimit = 0;
                endLimit = numberRowsByCore* (int)threadId;
                if (endLimit>lRows.size())
                    endLimit = lRows.size();

                //List <Row> lRowsTemp = lRows.subList(startLimit, endLimit);
                List <Row> lRowsTemp =  splitDataSet(lRows,startLimit,endLimit);
                        
                evaluateSetTweets(lRowsTemp,session);
                System.out.println("I am thread " + threadId + " I have finished");
                long elapsedTimeMillis = System.currentTimeMillis()-start;

                // Get elapsed time in seconds
                float elapsedTimeSec = elapsedTimeMillis/1000F;
                System.out.println("Tiempo en partes: " + elapsedTimeSec + " s hilo:" + threadId);
              }
            });
        }

        //detiene la posibilidad de crear mas tareas
        exec.shutdown();
        System.out.println("Tiempo de espera");
        //espera a que terminen todas las tareas
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        System.out.println("TERMINO");
        //}               
        // Get elapsed time in milliseconds
        //long elapsedTimeMillis = System.currentTimeMillis()-start;

        // Get elapsed time in seconds
        //float elapsedTimeSec = elapsedTimeMillis/1000F;
        //System.out.println("Tiempo en partes: " + elapsedTimeSec + " s");
        
        /*
        //Proceso en modo serial
        final long start = System.currentTimeMillis();
        String message ="";
        Long IdTweet;
        int i=1;
        for (Row row : results) {
            //System.out.println((i++) +" " + row.getString("user")+", "+row.getString("message"));
            
            IdTweet =  row.getLong("Id");
            message = row.getString("message");
            classifyNewTweet(message,session,IdTweet);
                    
        }

        // Get elapsed time in milliseconds
        long elapsedTimeMillis = System.currentTimeMillis()-start;

        // Get elapsed time in seconds
        float elapsedTimeSec = elapsedTimeMillis/1000F;
        System.out.println("Tiempo normal: " + elapsedTimeSec + " s");
        */
        }
        catch (Exception e)
        {
            System.out.println("ERROR: " +  e.getMessage());
        }

    }
    
    /*
            try{
            InputStream is = new FileInputStream(
                    "/home/mary/Codes/GetTheLeadMaven/src/main/models/en-token.bin");
 
            TokenizerModel model = new TokenizerModel(is);

            Tokenizer tokenizer = new TokenizerME(model);
            
            String tokens[] = tokenizer.tokenize(message);
                System.out.println("");
            for (String a : tokens)
                    System.out.println(a);

            is.close();
            }
            catch(Exception ex1){
                System.out.println("ERROR: " + ex1.getMessage().toString());
            }
            */
    
    public void trainModel() {
        /*
        InputStream dataIn = null;
        try {
            dataIn = new FileInputStream(
                    "/home/mary/Codes/GetTheLeadMaven/src/main/dataTraining/tweets.txt");
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);
            model = DocumentCategorizerME.train("en", sampleStream);//, cutoff,
        //            trainingIterations);
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            if (dataIn != null) {
                try {
                    dataIn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        }*/
        
        // model name you define your own name for the model
        String onlpModelPath = "/home/mary/Codes/GetTheLeadMaven/src/main/models/en-doccat.bin";
        // training data set
        String trainingDataFilePath = "/home/mary/Codes/GetTheLeadMaven/src/main/dataTraining/tweets.txt";

        InputStream dataInputStream = null;
        try {
            // Read training data file
            dataInputStream = new FileInputStream(trainingDataFilePath);
            // Read each training instance
            ObjectStream<String> lineStream = new PlainTextByLineStream(dataInputStream, "UTF-8");
            // making sample Stream to train
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            // Calculate the training model "en" means english, sampleStream is the training data, 2 cutoff, 300 iterations
            model = DocumentCategorizerME.train("en", sampleStream);//, 2, 30);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage() );
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (Exception e) {
                     System.out.println("ERROR: " + e.getMessage().toString() );
                }
            }
        }
 
 
        // Now we are writing the calculated model to a file in order to use the
        // trained classifier in production
 
        try {
            if (model != null) {
                //saving the file
                model.serialize(new FileOutputStream(onlpModelPath));
            }
        } catch (Exception e) {
             System.out.println("ERROR: " + e.getMessage().toString() );
        }
        
    }
    
    public void classifyNewTweet(String tweet,Session session,Long Id) {
        //IdTweet,UserId,CurrentUserRetweetId,InReplyToUserId
        try {
            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
            double[] outcomes = myCategorizer.categorize(tweet);
            String category = myCategorizer.getBestCategory(outcomes);
            String classification ="";
            if (category.equalsIgnoreCase("1")) {
                //System.out.println("The tweet is positive :) ");
                classification= "positive";
            } else {
                //System.out.println("The tweet is negative :( ");
                classification = "negative";
            }
            String query = "UPDATE GetTheLead." + TABLE + " " +
                    "SET classification = '" + classification + "' " + 
                    "WHERE Id = " + Id ;
            
            session.execute(query);
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    
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

    private int evaluateSetTweets(List<Row> lRows,Session session) {
        
        int i = 1;
        String message = "";
        Long IdTweet;
        /*
        for (Row row : results) {
        System.out.println((i++) +" " +
        row.getString("user")+", "+row.getString("message"));
        IdTweet =  row.getDouble("Id");
        message = row.getString("message");
        classifyNewTweet(message,session,IdTweet);
        }
         */
        for (Row row : lRows) {
            //System.out.println((i++) +" " +
            //      row.getString("user")+", "+row.getString("message"));
                     
            IdTweet =  row.getLong("Id");
            message = row.getString("message");
            classifyNewTweet(message,session,IdTweet);
        }
        
        return i;
    }
    
    public synchronized List<Row> splitDataSet(List <Row> result,int start, int end) {
        System.out.println("Limit start " + start + " end " + end);
        List <Row> listTemp = result.subList(start, end);
        return listTemp;
    }

}
