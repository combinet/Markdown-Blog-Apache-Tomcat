<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><!DOCTYPE html>
<html>
    <head>
        <title>Edit Post</title>
        <link href="editor.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
    	<h1>Edit Post</h1>
        <form action="post" method="POST">
        	<div>
        		<button type="submit" name="action" value="save">Save</button>
        		<button type="submit" name="action" value="list">Close</button>
        		<button type="submit" name="action" value="preview">Preview</button>
        		<button type="submit" name="action" value="delete">Delete</button>
        	</div>
            <input type="hidden" name="username" value="<%= request.getParameter("username") %>">
            <input type="hidden" name="postid" value="<%= request.getParameter("postid") %>">
        	<div class="content">
        		<label for="title">Title</label><br>
        		<input type="text" id="title" name="title" value="<%= request.getAttribute("title") %>">
        	</div>
        	<div class="content">
        		<label for="body">Body</label><br>
        		<textarea id="body" name="body"><%= request.getAttribute("body") %></textarea>
        	</div>
        </form>
    </body>
</html>
