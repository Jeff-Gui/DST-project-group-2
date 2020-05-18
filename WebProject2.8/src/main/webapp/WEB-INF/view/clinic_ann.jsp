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
    <div class="layui-header">
        <div class="layui-logo"> &#12288 &#12288 &#12288 Precision Medicine</div>
        <ul class="layui-nav layui-layout-left">
            <li class="layui-logo">Matching System &#12288 &#12288 &#12288 &#12288 &#160</li>
        </ul>

        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item">
                <% if (session.getAttribute("username") == null) { %>
                <a href="javascript:;"></a>
                <% } %>
                <% if (session.getAttribute("username") != null) { %>
                <a href="javascript:;">
                    Hi, <% out.print(session.getAttribute("username")); %>
                </a>
                <% } %>
            </li>

            <li class="layui-nav-item">
                <% if (session.getAttribute("username") == null) { %>
                <a href="<%=request.getContextPath()%>/signin">Sign in</a>
                <% } %>
                <% if (session.getAttribute("username") != null) { %>
                <a href="<%=request.getContextPath()%>/logout">Logout</a>
                <% } %>
            </li>
        </ul>
    </div>

    <div class="layui-side layui-bg-black">
        <div class="layui-side-scroll">
            <ul class="layui-nav layui-nav-tree layui-inline" lay-filter="demo" style="margin-right: 10px;">
                <li class="layui-nav-item layui-nav-itemed">
                    <a href="javascript:;">Matching System</a>
                    <dl class="layui-nav-child">
                        <dd><a href="<%=request.getContextPath()%>/">
                            <span data-feather="home"></span>
                            Dashboard <span class="sr-only">(current)</span>
                        </a></dd>
                        <dd><a href="<%=request.getContextPath()%>/matchingIndex">
                            <span data-feather="file"></span>
                            Matching
                        </a></dd>
                        <dd><a href="<%=request.getContextPath()%>/samples">
                            <span data-feather="file"></span>
                            Samples
                        </a></dd>
                    </dl>
                </li>
                <li class="layui-nav-item layui-nav-itemed">
                    <a href="javascript:;">Knowledge Base</a>
                    <dl class="layui-nav-child">
                        <dd><a href="<%=request.getContextPath()%>/drugs">
                            <span data-feather="file-text"></span>
                            Drugs
                        </a></dd>
                        <dd><a href="<%=request.getContextPath()%>/drugLabels">
                            <span data-feather="file-text"></span>
                            Drug Labels
                        </a></dd>
                        <dd><a href="<%=request.getContextPath()%>/dosingGuideline">
                            <span data-feather="file-text"></span>
                            Dosing Guideline
                        </a></dd>
                        <dd><a href="<%=request.getContextPath()%>/clinicAnn">
                            <span data-feather="file-text"></span>
                            Clinic Annotations (current)
                        </a></dd>
                    </dl>
                </li>
                <% Object username = session.getAttribute("username");%>
                <% if (username!=null&&username.equals("zju")) { %>
                <li class="layui-nav-item">
                    <a href="javascript:;">Website Managing</a>
                    <dl class="layui-nav-child">
                        <dd><a href="<%=request.getContextPath()%>/reset">
                            <span data-feather="file-text"></span>
                            Reset user password
                        </a></dd>
                    </dl>
                    <dl class="layui-nav-child">
                        <dd><a href="<%=request.getContextPath()%>/panel">
                            <span data-feather="file-text"></span>
                            Update knowledge base
                        </a></dd>
                    </dl>
                </li>
                <% } %>
                <li class="layui-nav-item"><a href="">Reload</a></li>
                <li class="layui-nav-item"><a href="<%=request.getContextPath()%>/aboutUs">About Us</a></li>
            </ul>
        </div>
    </div>

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
