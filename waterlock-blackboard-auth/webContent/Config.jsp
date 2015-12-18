<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@page import="gdp18.waterlock.Utils" %>

<%@ taglib prefix="bbNG" uri="/bbNG"%>

<bbNG:genericPage ctxId="ctx" >

	<%
		String page_title = "Waterlock Authentication Settings";
	
		String providerID = request.getParameter("providerID");
		String sharedKey = request.getParameter("sharedKey");
		
		if (providerID != null){
			if(!providerID.trim().equals("")){
		        Utils.pluginSettings.setProviderID(providerID.trim());
		    }
		}
		
		if (sharedKey != null){
			if(!sharedKey.trim().equals("")){
		        Utils.pluginSettings.setSharedKey(sharedKey.trim());
		    }
		}
		
		providerID = Utils.pluginSettings.getProviderID();
		sharedKey = Utils.pluginSettings.getSharedKey();
	%>
	<bbNG:pageHeader instructions="The settings for communication with Waterlock Authentication modules.">
		<bbNG:breadcrumbBar>
			<bbNG:breadcrumb><%= page_title %></bbNG:breadcrumb>
		</bbNG:breadcrumbBar>
		<bbNG:pageTitleBar ><%= page_title %></bbNG:pageTitleBar>
	</bbNG:pageHeader>
	
	<bbNG:form action="Config.jsp" method="post" onSubmit="return validateForm();">	
		<bbNG:dataCollection>
			<bbNG:step title="Settings">
				<bbNG:dataElement label="Provider ID" isRequired="true" labelFor="Provider ID">
					<bbNG:textElement name="providerID" value="<%= providerID %>" helpText="The name of this Blackboard instance"/>
				</bbNG:dataElement>
				<bbNG:dataElement label="Shared Key" isRequired="true" labelFor="Shared Key">
					<bbNG:textElement name="sharedKey" value="<%= sharedKey %>" helpText="The shared key to use for Single Sign On. The Waterlock module should use the same shared key. "/>
				</bbNG:dataElement>
			</bbNG:step>
			<bbNG:stepSubmit/>
		</bbNG:dataCollection>
	</bbNG:form>
</bbNG:genericPage>
