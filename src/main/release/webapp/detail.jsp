<%--
  **************************************************-
  InGrid iBus
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
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
<%@page import="net.weta.components.communication.tcp.TcpCommunication"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="de.ingrid.ibus.Bus" %>
<%@ page import="de.ingrid.ibus.registry.Registry" %>
<%@ page import="de.ingrid.utils.PlugDescription" %>
<%@ page import="java.lang.Exception" %>
<%@ page import="java.util.Enumeration" %>

<%!
Registry registry;
public PlugDescription getIPlug(String id) {
	Bus bus = Bus.getInstance();
	registry = bus.getIPlugRegistry();
	return registry.getPlugDescription(id);
}

public String formatTime(long timespan) {
    timespan /= 1000; // convert to seconds
    
    String output = "";
    long days    = timespan/24/60/60;
    long hours   = timespan/60/60%24;
    long minutes = timespan/60%60;
    long seconds = timespan%60;
    
    if (days != 0)
        output += days +"d ";
    if (hours != 0)
        output += hours +"h ";
    if (minutes != 0)
        output += minutes +"min ";
    if (seconds != 0)
        output += seconds +"sec";
    return output;
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="de.ingrid.utils.metadata.Metadata"%>
<%@page import="de.ingrid.utils.metadata.IPlugType"%>
<html>
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
<table class="table" width="900" align="center">
	<tr>
		<td colspan="5" class="tablehead">iPlug - Information</td>
	</tr>

<%
	PlugDescription description = getIPlug(request.getParameter("id"));
    TcpCommunication communication = (TcpCommunication) registry.getCommunication();
%>
		
		<tr>
            <td class="tablecell">Plug-ID:</td>
			<td class="tablecell"><%=description.getPlugId() %></td>
		</tr>
        <tr>
            <td class="tablecell">Remote-Address:</td>
            <td class="tablecell"><%=communication.getRemoteIpFrom(description.getProxyServiceURL()) %></td>
        </tr>
        <tr>
            <td class="tablecell">Registered since:</td>
            <td class="tablecell"><%=formatTime(communication.getTimeSinceRegistrationInMs(description.getProxyServiceURL())) %></td>
        </tr>
        <tr>
            <td class="tablecell">Last Lifesign:</td>
            <td class="tablecell"><%=formatTime(System.currentTimeMillis()-description.getLong("addedTimeStamp")) %></td>
        </tr>
        <tr>
            <td class="tablecell">Admin-Url:</td>
            <td class="tablecell"><a href="<%=description.getIplugAdminGuiUrl() %>"><%=description.getIplugAdminGuiUrl() %></a></td>
        </tr>
        <tr>
            <td class="tablecell">Admin-Port:</td>
            <td class="tablecell"><%=description.getIplugAdminGuiPort() %></td>
        </tr>
        <tr>
            <td class="tablecell">Data-types:</td>
            <td class="tablecell">
                <ul>
                <%
                String[] types = description.getDataTypes();
                for (int i=0; i < types.length; i++) {
                %>
                    <li><%=types[i] %></li>
                <%
                }
                %>
                </ul>
            </td>
        </tr>
        <tr>
            <td class="tablecell">Ranking:</td>
            <td class="tablecell">
                <ul>
                <%
                types = description.getRankingTypes();
                for (int i=0; i < types.length; i++) {
                %>
                    <li><%=types[i] %></li>
                <%
                }
                %>
                </ul>
            </td>
        </tr>
        <tr>
            <td class="tablecell">Partner:</td>
            <td class="tablecell">
                <ul>
                <%
                types = description.getPartners();
                for (int i=0; i < types.length; i++) {
                %>
                    <li><%=types[i] %></li>
                <%
                }
                %>
                </ul>
            </td>
        </tr>
        <tr>
            <td class="tablecell">Provider:</td>
            <td class="tablecell">
                <ul>
                <%
                types = description.getProviders();
                for (int i=0; i < types.length; i++) {
                %>
                    <li><%=types[i] %></li>
                <%
                }
                %>
                </ul>
            </td>
        </tr>
	</table>
    <table class="table" align="center">                    
        <tr align="center">
        <td>
            <input type="submit" value="Zur&uuml;ck"/>
        </td>
        </tr>
    </table>
</form>
</center>
</body>
</html>
