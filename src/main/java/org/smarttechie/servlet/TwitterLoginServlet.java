/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.smarttechie.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import java.io.IOException;
import java.util.Properties;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
 
public class TwitterLoginServlet extends HttpServlet {
 
private static final long serialVersionUID = 1L;
 
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
System.out.println( "TwitterLoginServlet:doGet" );
ConfigurationBuilder cb = new ConfigurationBuilder();
 
Properties props = new TwitterProperties().getProperties();
cb.setDebugEnabled(true)
.setOAuthConsumerKey((String)props.get("twitterConsumerKey"))
.setOAuthConsumerSecret((String)props.get("twitterConsumerSecret"))
.setOAuthRequestTokenURL((String)props.get("twitterRequestTokenURL"))
.setOAuthAuthorizationURL((String)props.get("twitterAuthorizeURL"))
.setOAuthAccessTokenURL((String)props.get("twitterAccessTokenURL"));
TwitterFactory tf = new TwitterFactory(cb.build());
Twitter twitter = tf.getInstance();
request.getSession().setAttribute("twitter", twitter);
try {
StringBuffer callbackURL = request.getRequestURL();
System.out.println( "TwitterLoginServlet:callbackURL:"+callbackURL );
int index = callbackURL.lastIndexOf("/");
callbackURL.replace(index, callbackURL.length(), "").append("/TwitterCallback");
 
RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
request.getSession().setAttribute("requestToken", requestToken);
System.out.println( "requestToken.getAuthenticationURL():"+requestToken.getAuthenticationURL() );
response.sendRedirect(requestToken.getAuthenticationURL());
 
} catch (TwitterException e) {
throw new ServletException(e);
}
 
}
 
/**
* @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
*      response)
*/
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
IOException {
System.out.println("Unexpected doPost ...");
}
}