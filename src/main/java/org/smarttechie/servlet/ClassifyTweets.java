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
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cluster cluster;
        Session session;
        cluster = Cluster.builder().addContactPoint("localhost").build();
        session = cluster.connect("GetTheLead");
        String message ="";
        trainModel();
        ResultSet results = session.execute("SELECT * FROM TweetsTest2 LIMIT 100");
        for (Row row : results) {
            System.out.println(
                    row.getString("user")+", "+row.getString("message"));
            
            message = row.getString("message");
            classifyNewTweet(message);
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
        
        }
        
        
    
    }
    
    public void trainModel() {
        /*
        InputStream dataIn = null;
        try {
            dataIn = new FileInputStream(
                    "/home/mary/Codes/GetTheLeadMaven/src/main/dataTraining/tweets.txt");
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);
            // Specifies the minimum number of times a feature must be seen
            int cutoff = 2;
            int trainingIterations = 300;
            model = DocumentCategorizerME.train("en", sampleStream, cutoff,
                    trainingIterations);
        } 
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: " + e.getMessage().toString());
        } finally {
            if (dataIn != null) {
                try {
                    dataIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR: " + e.getMessage().toString());
                }
            }
        }*/
        // model name you define your own name for the model
        String onlpModelPath = "en-doccat.bin";
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
            model = DocumentCategorizerME.train("en", sampleStream, 2, 30);
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
    
    public void classifyNewTweet(String tweet) {
        try {
            DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
            double[] outcomes = myCategorizer.categorize(tweet);
            String category = myCategorizer.getBestCategory(outcomes);

            if (category.equalsIgnoreCase("1")) {
                System.out.println("The tweet is positive :) ");
            } else {
                System.out.println("The tweet is negative :( ");
            }
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

}
