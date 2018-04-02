# Markdown-Blog-Apache-Tomcat
A Markdown blogging web application based on Apache Tomcat, MySQL and Java ServerPages (JSP).

This web application allows users to create a new post (written in markdown), preview the post (rendered in HTML), and manage existing posts. These tasks are supported through three main webpages: edit page, preview page, and list page.

## Edit page
<img src="https://raw.githubusercontent.com/zhengyuan-liu/Markdown-Blog-Apache-Tomcat/master/images/edit.png" width = "350" height = "345"/>
The "edit page" allows editing the title and body of a post.

* The page contains two input boxes

  1. a title <input> box of text type. This text input element must have the ID attribute with value "title".
  2. a body <textarea>. This textarea element must have the ID attribute with value "body".

* The page contains four buttons: save, close, preview, and delete. Once pressed,

  1. "save" button saves the content of the post to the database and goes to the "list page".
  2. "close" button goes to the "list page" without saving the current content.
  3. "preview" button goes to the "preview page" (without saving the current content).
  4. "delete" button deletes the post from the database and goes to the "list page".

## Preview page
<img src="https://raw.githubusercontent.com/zhengyuan-liu/Markdown-Blog-Apache-Tomcat/master/images/preview.png" width = "400" height = "370"/>
The "preview page" shows the HTML rendering of a post written in markdown. The page has a "close" button and once pressed, the close button goes back to the "edit page" of the post.

## List page
<img src="https://raw.githubusercontent.com/zhengyuan-liu/Markdown-Blog-Apache-Tomcat/master/images/list.png" width = "500" height = "250"/>
The "list page" shows the list of all blog posts saved by the user. The posts in the list is sorted by their "postid" (a unique integer assigned to a post) in the ascending order. Each item in the list shows:

1. title, creation, and modification dates of the post, and
2. two buttons: open and delete. Once pressed,
    * "open" button goes to the "edit page" for the post.
    * "delete" button deletes the post from the database and comes back to the list page.

The list page contains a "new post" button to allow users to create a new post. Once pressed, the button leads to the "edit page" for a new post.

## Server-Side API
This web application follows the following REST API:

    /editor/post?action=type&username=name&postid=num&title=title&body=body

The parameter "action" specifies one of five "actions" that your site has to take: open, save, delete, preview, and list. The other four parameters, username, postid, title, and body are (optional) parameters that the actions may need.
