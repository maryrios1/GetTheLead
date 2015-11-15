<%@page import="twitter4j.TwitterException"%>
<%@page import="twitter4j.User"%>
<%@page import="twitter4j.TwitterFactory"%>
<%@page import="twitter4j.Twitter"%>
<%@page import="twitter4j.auth.RequestToken"%>
<%@page import="twitter4j.auth.AccessToken"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">
function closeWindow(){
<%!private long userId;
private String screenName;
private String name;
private String avatarUrl;
private String selfDescription;%>

<%
 //   System.out.println("redirect.jsp");
   // Twitter twitter = TwitterFactory.getSingleton();
//(Twitter) request.getSession().getAttribute("twitter");
            
/*RequestToken requestToken = (RequestToken) request.getSession()
.getAttribute("requestToken");*/
    /*mary
    String verifier = request.getParameter("oauth_verifier");
    System.out.println("id " + twitter.getId());
    AccessToken accessToken = null;
    accessToken = twitter.getOAuthAccessToken();
    */
/* try {
accessToken = twitter.getOAuthAccessToken(requestToken,
verifier);
request.getSession().removeAttribute("requestToken");
} catch (Exception e){//(TwitterException twitterException) {
//twitterException.printStackTrace();
    System.out.println("error" + e.getMessage());
}*/
    /*mary
 User user = twitter.verifyCredentials();
userId = accessToken.getUserId();
            */
//User user = twitter.showUser(userId);
    /*mary
avatarUrl = user.getProfileImageURL().toString();
System.out.println(user.getScreenName());
            */
%>
//self.close();
}
</script>
</head>
<body> <!--onload="javascript:closeWindow()">-->
    <%/*=avatarUrl*/%>
    hola mary
</body>
