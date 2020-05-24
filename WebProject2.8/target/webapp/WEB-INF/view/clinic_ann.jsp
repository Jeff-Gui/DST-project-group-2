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
    <title>Clinic Annotation</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet"  href="static/layui/css/layui.css"  media="all">

<%--    <style>--%>
<%--        table{--%>
<%--            border: 1px solid black;--%>
<%--            width: 300px;--%>
<%--            /* 缩略版表格（废弃） */--%>
<%--            table-layout:fixed;--%>
<%--        }--%>
<%--        td{--%>
<%--            border: 1px solid black;--%>
<%--            overflow: hidden;--%>
<%--            word-break: keep-all;--%>
<%--            /* 内容超出宽度时隐藏超出部分的内容 */--%>
<%--            white-space:nowrap;--%>
<%--            /* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/--%>
<%--            text-overflow:ellipsis--%>
<%--        }--%>
<%--    </style>--%>

</head>
<body class="layui-layout-body">

<div class="layui-layout layui-layout-admin">
    <jsp:include page="nav.jsp" >
        <jsp:param name="active" value="clinicAnnotation" />
    </jsp:include>

    <div class="layui-body">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
            <legend>Clinic Annotations</legend>
        </fieldset>

        <div class="layui-form" style='overflow:auto'>
            <table class="layui-table" lay-even="" lay-skin="nob" >
                <colgroup>
                    <col width="150">
                    <col width="150">
                    <col width="200">
                    <col>
                </colgroup>
                <thead>
                <tr>
                    <th>#</th>
                    <th>Variant Gene</th>
                    <th>Related Chemicals</th>
                    <th>Related Diseases</th>
                    <th>Annotation</th>
                    <th>Level of Evidence</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${clinicAnn}" var="item" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td>${item.gene}</td>
                        <td>${item.related_chemicals}</td>
                        <td>${item.related_diseases}</td>
                        <td>${item.annotation_text}</td>
                        <td>${item.evidencelevel}</td>
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
