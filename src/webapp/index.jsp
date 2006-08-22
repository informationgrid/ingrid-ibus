<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="de.ingrid.ibus.Bus" %>
<%@ page import="de.ingrid.ibus.registry.Registry" %>
<%@ page import="de.ingrid.utils.PlugDescription" %>
<%@ page import="java.util.Enumeration" %>

<%!
public PlugDescription[] getIPlugs() {
	Bus bus = Bus.getInstance();
	Registry registry = bus.getIPlugRegistry();
	PlugDescription[] description = registry.getAllIPlugsWithoutTimeLimitation();

	return description;
}
%>

<%
String submitted = request.getParameter("submitted");
Enumeration paramNames = request.getParameterNames();
boolean saved = false;

if ((submitted != null) && submitted.equals("true")) {
	Bus bus = Bus.getInstance();
	Registry registry = bus.getIPlugRegistry();

	while (paramNames.hasMoreElements()) {
	    String paramName = (String) paramNames.nextElement();
	    if (paramName.endsWith("isActivated")) {
	    	String paramValue = request.getParameter(paramName);
	    	final String iplugName = paramName.substring(0, paramName.lastIndexOf("isActivated"));

		    if (paramValue.equals("true")) {
			    registry.activatePlug(iplugName);
		    } else {
			    registry.deActivatePlug(iplugName);
		    }
		}
	}

    saved = true;
}

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>IBus Administration</title>
<link href="css/admin.css" rel="stylesheet" type="text/css" />
</head>
<body>

<%if (saved) {%>
	<div class="error">Änderungen gespeichert.</div>
<%}%>

<form method="get" action="index.jsp">
	<table class="table" width="400" align="center">
	<tr>
		<td colspan="2" class="tablehead">An-/Abschalten von IPlugs.</td>
	</tr>
<%
	PlugDescription[] descriptions = getIPlugs();

	for (int i=0; i<descriptions.length; i++) {
%>
	<tr>
		<td class="tablecell" width="100"><%=descriptions[i].getProxyServiceURL()%></td>
		<td class="tablecell" width="100">
			<select name="<%=descriptions[i].getProxyServiceURL()%>isActivated">
				<option value="true" <%if(descriptions[i].isActivate()) {%>selected<%}%> >an</option>
				<option value="false" <%if(!descriptions[i].isActivate()) {%>selected<%}%> >aus</option>
			</select>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<input type="hidden" name="submitted" value="true">
			<input type="submit" value="Speichern"/>
		</td>
	</tr>
	</table>
</form>
<%
	}
%>
</body>
</html>
