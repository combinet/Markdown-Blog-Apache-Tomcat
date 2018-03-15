import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
import java.io.*;
import java.util.*;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class Editor extends HttpServlet implements Servlet {
	
	public Editor() {}
	
	private static final String user = "cs144";
	private static final String password = "";
	
	Connection conn = null;
	PreparedStatement preparedStmt = null;
	ResultSet rs = null;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action.equals("open")) {  // open action
			openAction(request, response);
		}
		else if (action.equals("preview")) {  // preview action
			previewAction(request, response);
		}
		else if (action.equals("list")) {  // list action
			listAction(request, response);
		}
		else {
			String reason = "Action " + action + " cannot be issued via GET method";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");	
		if (action.equals("open")) {  // open action
			openAction(request, response);
		}
		else if (action.equals("save")) {  // save action
			saveAction(request, response);
		}
		else if (action.equals("delete")) {  // delete action
			deleteAction(request, response);
		}
		else if (action.equals("preview")) {  // preview action
			previewAction(request, response);
		}
		else if (action.equals("list")) {  // list action
			listAction(request, response);
		}
		else {
			String reason = "Action " + action + " cannot be issued via POST method";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
		}
	}
	
	/**
	 * return the "edit page" for the post with the given postid by the user
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void openAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// required parameters: username and postid
		String name = request.getParameter("username");
		String id = request.getParameter("postid");
		if (name == null || id == null) {
			String reason = "Invalid parameter in open action";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		try {
			int postid = Integer.parseInt(id);
			String title = request.getParameter("title");
			String body = request.getParameter("body");
			if (title == null && body == null) {  // If title and body parameters are both missing
				// set the title and body to empty strings
				title = "";
				body = "";
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", user, password); 
				preparedStmt = conn.prepareStatement("SELECT * FROM Posts WHERE username = ? AND postid = ?");
				preparedStmt.setString(1, name);
				preparedStmt.setInt(2, postid);
				rs = preparedStmt.executeQuery();
				if (rs.next()) {  // if (username, postid) row exists in the database
					// retrieve the title and body from the database
				    title = rs.getString("title");
				    body = rs.getString("body");
				}	
			}// If title and body parameters have been passed, use the passed parameter values as the initial title and body values.
			if (title == null)  // only body is passed and title is missing
				title = "";
			if (body == null)  // only title is passed and body is missing
				body = "";
			request.setAttribute("title", title);
			request.setAttribute("body", body);
		}
		catch (NumberFormatException e) {
			String reason = "Invalid postid";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		catch (SQLException ex) {
			String reason = "SQLException caught";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		finally {
			try { preparedStmt.close(); } catch (Exception e) {  }
			try { conn.close(); } catch (Exception e) {  }
		}
		request.getRequestDispatcher("/edit.jsp").forward(request, response);
	}
	
	/**
	 * save the post into the database and go to the "list page" for the user
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void saveAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// required parameters: username, postid, title, and body
		String name = request.getParameter("username");
		String id = request.getParameter("postid");
		String title = request.getParameter("title");
		String body = request.getParameter("body");
		int postid = Integer.parseInt(id);
		try {  // save the post into the database
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", user, password);
			if (postid <= 0) {
				// assign a new postid
				preparedStmt = conn.prepareStatement("SELECT MAX(postid) FROM Posts WHERE username = ?");
				preparedStmt.setString(1, name);
				rs = preparedStmt.executeQuery();
				String maxid = "";
				int newid = 1;
				if (rs.next()) {
					maxid = rs.getString("MAX(postid)");
					if(maxid != null)
						newid = Integer.parseInt(maxid) + 1;
				}
				preparedStmt.close();
				//save the content as a "new post"
				preparedStmt = conn.prepareStatement("INSERT INTO Posts VALUES (?, ?, ?, ?, ?, ?)");
				preparedStmt.setString(1, name);
				preparedStmt.setInt(2, newid);
				preparedStmt.setString(3, title);
				preparedStmt.setString(4, body);
				Timestamp now = new Timestamp(new java.util.Date().getTime());
				preparedStmt.setTimestamp(5, now);
				preparedStmt.setTimestamp(6, now);
				preparedStmt.executeUpdate();
			}
			else {
				preparedStmt = conn.prepareStatement("SELECT * FROM Posts WHERE username = ? AND postid = ?");
				preparedStmt.setString(1, name);
				preparedStmt.setInt(2, postid);
				rs = preparedStmt.executeQuery();
				if (rs.next()) {  // if (username, postid) row exists in the database
					preparedStmt.close();
					// update the row with new title, body, and modification date
					preparedStmt = conn.prepareStatement(
						"UPDATE Posts SET title = ?, body = ?, modified = ? WHERE username = ? AND postid = ?"
					);	
					preparedStmt.setString(1, title);
					preparedStmt.setString(2, body);
					Timestamp now = new Timestamp(new java.util.Date().getTime());
					preparedStmt.setTimestamp(3, now);
					preparedStmt.setString(4, name);
					preparedStmt.setInt(5, postid);
					preparedStmt.executeUpdate();
				}  // if (username, postid) row does not exist, do not make any change to the database
			}
		}
		catch (SQLException ex) {
			String reason = "SQLException caught";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		finally {
			try { preparedStmt.close(); } catch (Exception e) {  }
			try { conn.close(); } catch (Exception e) {  }
		}
		listAction(request, response);  // go to the "list page" for the user
	}
	
	/**
	 * delete the corresponding post and go to the "list page"
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void deleteAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("username");
		String id = request.getParameter("postid");
		try {   // delete the corresponding post
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", user, password); 
			preparedStmt = conn.prepareStatement("DELETE FROM Posts WHERE username = ? AND postid = ?");
			preparedStmt.setString(1, name);
			preparedStmt.setString(2, id);
			preparedStmt.executeUpdate();
		}
		catch (SQLException ex) {
			String reason = "SQLException caught";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		finally {
			try { preparedStmt.close(); } catch (Exception e) {  }
			try { conn.close(); } catch (Exception e) {  }
		}
		listAction(request, response);  // go to the "list page" for the user
	}
	
	/**
	 * return the "preview page" with the html rendering of the given title and body
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void previewAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// required parameters: username, postid, title, and body
		String name = request.getParameter("username");
		String id = request.getParameter("postid");
		String title = request.getParameter("title");
		String body = request.getParameter("body");
		if (name == null || id == null || title == null || body == null) {
			String reason = "Invalid parameter in preview action";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		try {
			Integer.parseInt(id);
		}
		catch (NumberFormatException e) {
			String reason = "Invalid postid";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		// return the "preview page" with the html rendering of the given title and body
		Parser parser = Parser.builder().build();
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		String title_render = "<h1>" + renderer.render(parser.parse(title)) + "</h1>";
		String body_render = renderer.render(parser.parse(body));
		String markdown = title_render + body_render;
		request.setAttribute("markdown", markdown);
		request.getRequestDispatcher("/preview.jsp").forward(request, response);
	}
	
	/**
	 * return the "list page" for the user
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void listAction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// required parameters: username
		String name = request.getParameter("username");
		if (name == null) {
			String reason = "Invalid parameter in list action";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/CS144", user, password); 
			preparedStmt = conn.prepareStatement("SELECT * FROM Posts WHERE username = ?");
			preparedStmt.setString(1, name);
			rs = preparedStmt.executeQuery();
			List<List<String>> table = new ArrayList<List<String>>();
			while (rs.next()) {
				String id = rs.getString("postid");
				String title = rs.getString("title");
			    String created = rs.getString("created");
			    String modified = rs.getString("modified");
			    List<String> row = new ArrayList<String>();
			    row.add(id);
			    row.add(title);
			    row.add(created);
			    row.add(modified);
			    table.add(row);
			}
			request.setAttribute("table", table);
		}
		catch (SQLException ex) {
			String reason = "SQLException caught";
			request.setAttribute("reason", reason);
			request.getRequestDispatcher("/invalidRequest.jsp").forward(request, response);
			return;
		}
		finally {
			try { preparedStmt.close(); } catch (Exception e) {  }
			try { conn.close(); } catch (Exception e) {  }
		}
		request.getRequestDispatcher("/list.jsp").forward(request, response);
	}
}
