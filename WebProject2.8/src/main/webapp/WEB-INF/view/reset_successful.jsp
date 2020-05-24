<%--
  Created by IntelliJ IDEA.
  User: hello
  Date: 2019-12-3
  Time: 15:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>

<!doctype html>
<html>
<head>
    <meta charset="utf-8">
<title>Reset Successful</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet"  href="static/layui/css/layui.css"  media="all">

</head>
<body class="layui-layout-body">

<div class="layui-layout layui-layout-admin">
    <jsp:include page="nav.jsp" >
        <jsp:param name="active" value="x" />
    </jsp:include>

    <div class="layui-body">
        <div class="row">
            <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
                <legend>Reset successful</legend>
            </fieldset>

            <blockquote class="layui-elem-quote">
                <div class="table-responsive">
                    User email: <% out.print(session.getAttribute("ResetEmail")); %>
                </div>
                <div class="table-responsive">
                    Username: <% out.print(session.getAttribute("ResetUsername")); %>
                </div>
                <div class="table-responsive">
                    New password: <% out.print(session.getAttribute("ResetPassword")); %>
                </div>
            </blockquote>
        </div>
    </div>

    <div class="layui-footer">
        ⭐️ DST Group 2 ⭐️
    </div>
</div>
    <script src="static/layui/layui.js"></script>
    <script>
        layui.use('element', function(){
            var element = layui.element;

        });
    </script>
</body>
</html>
