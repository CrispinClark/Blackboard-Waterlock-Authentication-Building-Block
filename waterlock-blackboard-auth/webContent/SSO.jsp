<%@page language="java" pageEncoding="ISO-8859-1" %>

<%@page import="blackboard.platform.*"%>
<%@page import="blackboard.platform.security.authentication.servlet.*"%>
<%@page import="blackboard.platform.session.*"%>
<%@page import="blackboard.data.user.User" %>

<%@page import="gdp18.waterlock.Utils" %>
<%@page import="gdp18.waterlock.Settings" %>

<%@page import="java.net.URLEncoder"%>
<%@page import="java.io.IOException"%>
<%@page import="java.security.InvalidKeyException"%>
<%@page import="java.security.NoSuchAlgorithmException"%>
<%@page import="java.security.SignatureException"%>
<%@page import="java.util.Map"%>

<%@page import="com.auth0.jwt.*"%>

<%@taglib uri="/bbData" prefix="bbData"%>

<bbData:context id="ctx">
	<%
		Utils.log("Someone has visited SSO.jsp");
	
		String jwtString = request.getParameter("jwt");	
	
	/*	String action = request.getParameter("action");
		boolean relogin = (action != null) && action.equals("relogin")/* && Utils.pluginSettings.getRefreshLogins();
    */
		BbSession bbSession = BbSessionManagerServiceFactory.getInstance().getSession(request);
		if (!bbSession.isAuthenticated() || bbSession.getUserName().equals("guest")/* || relogin*/){
			String selfURL = request.getRequestURI()+"?jwt=" + jwtString;

			String returnURL = URLEncoder.encode(selfURL, "UTF-8");

			String loginURL = "/webapps/login?action=relogin&new_loc=" + returnURL; 
			
			response.sendRedirect(loginURL);
			return;
		}
		/*
		// Reproduce canonically-ordered incoming auth payload.
		String requestAuthPayload = "serverName=" + serverName + "&expiration=" + expiration;
		*/
		
		try 
		{
			%><%=jwtString %><%
			
			Map<String, Object> decodedPayload = Utils.validateIncomingWebToken(jwtString);
			
			String serviceName = (String) decodedPayload.get("service_name");
			String callbackURL = (String) decodedPayload.get("next");

			User user = ctx.getUser();
			
			String username = Utils.decorateBlackboardUserName(user.getUserName());
			String firstname = user.getGivenName();
			String surname = user.getFamilyName();
			String email = user.getEmailAddress();
			
			//Generate the web token to be sent back to Synote for authentication
			String responseJWT = Utils.generateResponseJWT(username, firstname, surname, email);
			
			// Work around double-decoding bug coming from the Blackboard login page.
			// Check for unescaped URL chars ':' '/' or '?' in ReturnUrl and re-escape if found.  
			//callbackURL = Utils.checkAndEscapeTerminalUrlParam(callbackURL, "ReturnUrl");

			String separator = (callbackURL.contains("?") ? "&" : "?");
			String redirectURL = callbackURL + separator + "jwt=" + responseJWT;  
			
			// Redirect to Panopto login page.
			response.sendRedirect(redirectURL);
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			Utils.log(e, "Invalid Key");
			String s = Utils.getStackTrace(e);
			%> ERROR <%=s%><% 
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			Utils.log(e, "No such algorithm");
			String s = Utils.getStackTrace(e);
			%> ERROR <%=s%><%
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Utils.log(e, "Illegal State");
			String s = Utils.getStackTrace(e);
			%> ERROR <%=s%><%
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			Utils.log(e, "Signature err");
			String s = Utils.getStackTrace(e);
			%> ERROR <%=s%><%
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Utils.log(e, "IO Err");
			String s = Utils.getStackTrace(e);
			%> E0R <%=s%><%
		} catch (JWTVerifyException e) {
			// TODO Auto-generated catch block
			Utils.log(e, "JWT Verify error");
			String s = Utils.getStackTrace(e);
			%> ERROR!!! <%=s%><%
		}
	%>
</bbData:context>