<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="de.ingrid.ibus.Bus" %>
<%@ page import="de.ingrid.ibus.registry.Registry" %>
<%@ page import="de.ingrid.utils.PlugDescription" %>
<%@ page import="java.lang.Exception" %>
<%@ page import="java.util.Enumeration" %>

<%!
public Map getIPlugs() {
	Bus bus = Bus.getInstance();
	Registry registry = bus.getIPlugRegistry();
	PlugDescription[] description = registry.getAllIPlugsWithoutTimeLimitation();
	Map map = new HashMap(); 
	
	for(int i = 0; i < description.length; i++) {
		PlugDescription plugDescription = description[i];
		
		Metadata metadata = plugDescription.getMetadata();
		if(metadata == null) {
			metadata = new Metadata(IPlugType.OTHER, new Date(0), "Unknown");
			plugDescription.setMetadata(metadata);
		}
	    if(!map.containsKey(metadata.getPlugType())) {
	    	map.put(metadata.getPlugType(), new ArrayList());
	    }
	    List list = (List) map.get(metadata.getPlugType());
	    list.add(plugDescription);
	}
	return map;
}
%>

<%
String submitted = request.getParameter("submitted");
String cancel = request.getParameter("cancel");

Enumeration paramNames = request.getParameterNames();
boolean saved = false;
boolean canceled = false;

if ((cancel != null) && cancel.equals("true")) {
	canceled = true;
}

if ((submitted != null) && submitted.equals("true")) {
	Bus bus = Bus.getInstance();
	Registry registry = bus.getIPlugRegistry();

	while (paramNames.hasMoreElements()) {
	    String paramName = (String) paramNames.nextElement();
	    if (paramName.endsWith("isActivated")) {
	    	String paramValue = request.getParameter(paramName);
	    	final String iplugName = paramName.substring(0, paramName.lastIndexOf("isActivated"));

	    	try {
		    	if (paramValue.equals("true")) {
			    	registry.activatePlug(iplugName);
		    	} else {
			    	registry.deActivatePlug(iplugName);
		    	}
	    	} catch (Exception e) {
	    	    final String error = "Problem w&#x00E4;hrend der De-/Aktivierung eines IPlugs: ".concat(e.getLocalizedMessage());
	    	    %><center><div class="error"><%=error%><br/></br></div></center><%
	    	}
		}
	}

    saved = true;
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="de.ingrid.utils.metadata.Metadata"%>
<%@page import="de.ingrid.utils.metadata.IPlugType"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>IBus Administration</title>
<link href="<%=response.encodeURL("css/admin.css")%>" rel="stylesheet" type="text/css" />
<style type="text/css">
	body, td {font-family:Arial; font-size:12px}
</style>
</head>
<body>
<center>
<form method="post" action="<%=response.encodeURL("index.jsp")%>">
<input type="hidden" name="submitted" value="true">
<table class="table" width="900" align="center">
	<tr>
		<td colspan="5" class="tablehead">An-/Abschalten von IPlugs.</td>
	</tr>

<%if (saved)  {%>
<div style="background-color:#E4FFBC; color:#2B7E12; font-family: Arial; font-weight: bold; font-size: 12px; border: 1px solid #2B7E12; width:400px"><br/><br/>&#x00C4;nderungen gespeichert.<br/><br/></div>
<br/>
<%} else if (canceled) {%> 	
<div class="error">&#x00C4;nderungen verworfen.</div>
<br/>
<%}%>


<%
	Map descriptions = getIPlugs();
	Set keySet = descriptions.keySet();
	Iterator it = keySet.iterator();
	while(it.hasNext()){
		IPlugType plugType = (IPlugType) it.next();
		%>
		<tr>
			<td>&nbsp</td>
			<td>&nbsp</td>
			<td>&nbsp</td>
			<td>&nbsp</td>
			<td>&nbsp</td>
		</tr>
		<tr>
			<td><%=plugType%></td>
			<td>&nbsp</td>
			<td>&nbsp</td>
			<td>&nbsp</td>
			<td>&nbsp</td>
		</tr>
		<tr>
			<td class="tablecell">Proxy Service Url</td>
			<td class="tablecell">Name der Datenquelle</td>
			<td class="tablecell">Version</td>
			<td class="tablecell">Release Datum</td>
			<td class="tablecell">An /Aus</td>
		</tr>
		<%
		List plugs = (List) descriptions.get(plugType);
		for (int i=0; i<plugs.size(); i++) {
			PlugDescription plugDescription = (PlugDescription) plugs.get(i);
			Metadata metadata = plugDescription.getMetadata();
			%>
				<tr>
					<td class="tablecell" width="100"><%=plugDescription.getProxyServiceURL()%></td>
					<td class="tablecell" width="100"><%=plugDescription.getDataSourceName()%></td>
					<td class="tablecell" width="100"><%=metadata.getVersion()%></td>
					<td class="tablecell" width="100"><%=new SimpleDateFormat("yyyy-MM-dd").format(metadata.getReleaseDate())%></td>
					<td class="tablecell" width="100">
						<select name="<%=plugDescription.getProxyServiceURL()%>isActivated">
							<option value="true" <%if(plugDescription.isActivate()) {%>selected="selected"<%}%> >an</option>
							<option value="false" <%if(!plugDescription.isActivate()) {%>selected="selected"<%}%> >aus</option>
						</select>
					</td>
				</tr>
				<tr>
					<td>&nbsp</td>
					<td>&nbsp</td>
					<td>&nbsp</td>
					<td>&nbsp</td>
					<td>&nbsp</td>
				</tr>
			<%
		}
	}
%>		

	</table>
	<br/>
	<table class="table" align="center">					
		<tr align="center">
		<td>
			<input type="hidden" name="cancel" value="true" />
			<input type="reset" value="Abbrechen" name="cancel" />
		</td>
		<td>
			<input type="submit" value="Weiter"/>
		</td>
		</tr>
	</table>
</form>
</center>
</body>
</html>
