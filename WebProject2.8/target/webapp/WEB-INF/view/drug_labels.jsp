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
    <title>Drug Labels</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet"  href="static/layui/css/layui.css"  media="all">

</head>
<body class="layui-layout-body">

<div class="layui-layout layui-layout-admin">
    <jsp:include page="nav.jsp" >
        <jsp:param name="active" value="drugLabels" />
    </jsp:include>

    <div class="layui-body">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
            <legend>Drug Labels</legend>
        </fieldset>

        <div class="layui-form">
            <table class="layui-table" lay-even="" lay-skin="nob">
                <colgroup>
                    <col width="150">
                    <col width="150">
                    <col width="200">
                    <col>
                </colgroup>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Drug Name</th>
                    <th>Source</th>
                    <th>Has Alternative Drug</th>
                    <th>Summary Markdown</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${drugLabels}" var="item" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td>${item.drugName}</td>
                        <td>${item.source}</td>
                        <td>${item.hasAlternativeDrug}</td>
                        <td>${item.summary_markdown}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
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
