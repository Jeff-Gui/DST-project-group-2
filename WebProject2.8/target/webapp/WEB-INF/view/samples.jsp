<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: hello
  Date: 2019-12-3
  Time: 15:37
  To change this template use File | Settings | File Templates.
--%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>Samples</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/layui/css/layui.css" media="all">

</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <jsp:include page="nav.jsp" >
        <jsp:param name="active" value="samples" />
    </jsp:include>

    <div class="layui-body">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
            <legend>Uploaded Samples</legend>
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
                    <th>Description</th>
                    <th>Uploaded By</th>
                    <th>Uploaded At</th>
                    <th>Sample Type</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${samples}" var="item" varStatus="loop">
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.description}</td>
                        <td>${item.uploadedBy}</td>
                        <td>${item.createdAt}</td>
                        <td>${item.sampleType}</td>
                        <td><a href="matching?sampleId=${item.id}&sampleType=${item.sampleType}">
                            <button type="button" class="layui-btn layui-btn-sm layui-btn-normal">Match</button>
                        </a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="layui-footer">
        DST Group 2
    </div>
</div>
<script src="${pageContext.request.contextPath}/static/layui/layui.js"></script>
    <script>
        layui.use('element', function(){
            var element = layui.element;

        });
    </script>
</body>
</html>
