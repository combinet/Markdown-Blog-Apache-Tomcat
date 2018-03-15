<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<%@ page import="java.util.*" %>
<html>
    <head>
        <title>Post List</title>
        <link href="editor.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <form action="post" method="POST">
        	<button type="submit" name="action" value="open">New Post</button>
            <input type="hidden" name="username" value=<%= request.getParameter("username") %> >
            <input type="hidden" name="postid" value="0" %>
        </form>
        <% List<List<String>> table = (List<List<String>>)request.getAttribute("table"); %>
        <table>
            <tr>
                <th>Title</th>
                <th>Created</th>
                <th>Modified</th>
                <th></th>
            </tr>
            <% for (int i=0; i<table.size(); i++){ 
                List<String> row = table.get(i); %>
            <tr>
                <td> <%= row.get(1) %> </td>
                <td> <%= row.get(2) %> </td>
                <td> <%= row.get(3) %> </td>
                <td>
                    <form action="post" method="POST">
                        <button type="submit" name="action" value="open" class="open">Open</button>
                        <button type="submit" name="action" value="delete" class="delete">Delete</button>
                        <input type="hidden" name="username" value=<%= request.getParameter("username") %>>
                        <input type="hidden" name="postid" value=<%= row.get(0) %>>
                    </form>
                </td>
            </tr>
            <% } %>
    </body>
</html>
