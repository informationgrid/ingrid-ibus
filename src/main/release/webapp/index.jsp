<%--
  **************************************************-
  InGrid iBus
  ==================================================
  Copyright (C) 2014 - 2016 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<%@page import="de.ingrid.utils.queryparser.QueryStringParser"%>
<%@page import="de.ingrid.utils.IngridQueryTools"%>
<%@page import="de.ingrid.utils.query.FieldQuery"%>
<%@page import="de.ingrid.ibus.debug.DebugEvent"%>
<%@page import="de.ingrid.utils.query.TermQuery"%>
<%@page import="de.ingrid.utils.query.IngridQuery"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="de.ingrid.ibus.Bus" %>
<%@ page import="de.ingrid.ibus.registry.Registry" %>
<%@ page import="de.ingrid.utils.PlugDescription" %>
<%@ page import="java.lang.Exception" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="de.ingrid.ibus.debug.DebugQuery" %>

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

private DebugQuery activateDebug() {
    DebugQuery debugQ = Bus.getInstance().getDebugInfo(); 
    debugQ.setActiveAndReset();
    return debugQ;
}

public DebugQuery search(String q) {
    DebugQuery debugQ = activateDebug();
    try {
        IngridQuery query = QueryStringParser.parse( q );    
        System.out.println( "Query: " + query );
        Bus.getInstance().searchAndDetail( query, 10, 0, 0, 30000, null );
    } catch (Exception e) {
        debugQ.addEvent( new DebugEvent( "Error", e.toString(  ) ) );
    }
    
    return debugQ;
}
%>

<%
String submitted = request.getParameter("submitted");
String cancel = request.getParameter("cancel");
String debug = request.getParameter("debug");
String query = request.getParameter("query");
String debugQueryString = request.getParameter("query") != null ? request.getParameter("query") : "ranking:score cache:off";
String fetch = request.getParameter("fetch");

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
DebugQuery debugInfo = null; 
String timeoutMsg = null;
if (debug != null && debug.equals("true") && fetch == null) {
    debugInfo = search( query );
}

if (fetch != null && fetch.equals("Fetch next query")) {
    DebugQuery debugQ = activateDebug();
    // wait max. 30s
    int times = 1;
    while (debugQ.isActive() && times < 10) {
        Thread.sleep( 1000 );
        times++;
    }
    if (debugQ.isActive()) {
        debugQ.setInactive();
        timeoutMsg = "Waited for an incoming query for 10s ... aborted!";
    } else {
        debugInfo = debugQ;
    }
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
    .section { font-weight: bold; background-color: #666; color: white; padding-left: 5px;}
    .header { font-weight: bold; }
    .debug { border: 1px solid #888; font-family: monospace; padding: 10px; }
    .debug ul { text-align: left; }
    .debug li { line-height: 25px; }
</style>
</head>
<body>
<center>
<form method="post" action="<%=response.encodeURL("index.jsp")%>">
<input type="hidden" name="submitted" value="true">
<table class="table" width="900" align="center">
	<tr>
		<td colspan="6" class="tablehead">An-/Abschalten von IPlugs.</td>
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
			<td>&nbsp</td>
		</tr>
		<tr>
			<td colspan="6" class="section"><%=plugType%></td>
		</tr>
		<tr class="header">
            <td class="tablecell"></td>
			<td class="tablecell">Proxy Service Url</td>
			<td class="tablecell">Name der Datenquelle</td>
			<td class="tablecell">Version</td>
			<td class="tablecell">Release Datum</td>
			<td class="tablecell">An / Aus</td>
		</tr>
		<%
		List plugs = (List) descriptions.get(plugType);
		for (int i=0; i<plugs.size(); i++) {
			PlugDescription plugDescription = (PlugDescription) plugs.get(i);
			Metadata metadata = plugDescription.getMetadata();
			%>
				<tr>
                    <td class="tablecell" width="50"><a href="detail.jsp?id=<%=plugDescription.getPlugId()%>">info</a></td>
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
<form method="post" action="<%=response.encodeURL("index.jsp")%>" class="debug">
	<input type="hidden" name="debug" value="true" />
    <input type="text" name="query" style="width: 400px;" value="<%=debugQueryString%>"/>
    <input type="submit" value="Test Query"/>
    <input type="submit" name="fetch" value="Fetch next query" title="Wait for the next remotely executed query and analyze it (max. 10s)"/>
    <%if (timeoutMsg != null)  {%>
    <p><%=timeoutMsg%></p>
    <% } %>
    
    <%if (debugInfo != null)  {%>
        <h5>Query: <%=debugInfo.getQuery()%></h5>
        <ul>
        <% List events = debugInfo.getEvents(); %>
        <% for( int i=0; i < events.size(); i++ ) { %>
            <% DebugEvent event = (DebugEvent)events.get( i ); %>
            <li><span style="font-weight: bold;"><%=event.title%>:</span> <%=event.message%> <%if (event.duration != null)  {%> (took: <%=event.duration%>ms) <% } %>
            <%if (event.messageList != null)  {%>
                <ul>
                <% List messages = event.messageList; %>
                <% for( int j=0; j < messages.size(); j++ ) { %>
                    <li><%=messages.get(j)%></li>
                <% } %>
                </ul>
            <% } %>
            </li>
        <% } %>
        </ul>  
    <%}%>
    
</form>
</center>
</body>
</html>
