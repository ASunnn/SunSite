<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    <title>My JSP 'bookInsert.jsp.jsp' starting page</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <script src="<%=basePath%>js/jquery-1.11.3.min.js"></script>
    <script src="<%=basePath%>js/init.js"></script>

    <script type="text/javascript">
        function deletes() {
            $.ajax({
                url: baseRoot + "/book/delete",
                type: "post",
                data: JSON.stringify({
                    "bookid": $("#deletebookid").val(),
                }),
                contentType: "application/json",
                dataType: "json",
                success: function (data) {
                    if (data.flag == 1000) {
                        alert("删除成功");
                    }
                },
                error: function (e) {
                    alert("删除失败");
                }
            });
        }

        function addbook() {
            $.ajax({
                url: baseRoot + "/book/insert",
                type: "post",
                data: JSON.stringify({
                    "bookid": $("#insertbookid").val(),
                    "title": $("#title").val(),
                    "author": $("#author").val(),
                    "publisher": $("#publisher").val(),
                    "price": $("#price").val()
                }),
                contentType: "application/json",
                dataType: "json",
                success: function (data) {
                    if (data.flag == 1000) {
                        alert("插入成功");
                    }
                },
                error: function (e) {
                    alert("插入失败");
                }
            });
        }
    </script>
</head>
<h1>查询书籍</h1>
<body>
<h3>请输入书号:</h3>
<form action="bookquery" method="post">
    <input type="text" id="bookid" name="bookid">
    <!-- <button onclick="test()" value="提交"> -->
    <input type="submit" value="提交">
</form>
<h1>插入书籍</h1>
<h3>请输入图书信息：</h3>
<form>
    <table>
        <tr>
            <td>书号</td>
            <td><input type="text" name="insertbookid" id="insertbookid"></td>
        </tr>
        <tr>
            <td>书名</td>
            <td><input type="text" name="title" id="title"></td>
        </tr>
        <tr>
            <td>作者</td>
            <td><input type="text" name="author" id="author"></td>
        </tr>
        <tr>
            <td>出版社</td>
            <td><input type="text" name="publisher" id="publisher"></td>
        </tr>
        <tr>
            <td>单价</td>
            <td><input type="text" name="price" id="price"></td>
        </tr>
        <tr>
            <td><input type="button" onclick="addbook()" value="确定"/></td>
            <td><input type="reset" value="重置"></td>
        </tr>
    </table>
</form>
<h1>删除书籍</h1>
<h3>请输入书号:</h3>
<input type="text" id="deletebookid" name="deletebookid">
<input type="button" onclick="deletes()" value="删除"/>
</body>
</html>
