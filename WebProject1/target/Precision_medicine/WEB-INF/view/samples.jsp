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
    <title>Layui</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet"  href="static/layui/css/layui.css"  media="all">

</head>
<body class="layui-layout-body">
<%--<jsp:include page="head.jsp" />--%>
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">

        <div class="layui-logo"> &#12288 &#12288 &#12288 Precision Medicine</div>
        <!-- 头部区域（可配合layui已有的水平导航） -->
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
            <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
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
                <li class="layui-nav-item">
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
                            Clinic Annotations
                        </a></dd>
                    </dl>
                </li>
                <li class="layui-nav-item"><a href="">Reload</a></li>
                <li class="layui-nav-item"><a href="<%=request.getContextPath()%>/aboutUs">About Us</a></li>
            </ul>
        </div>
    </div>

    <div class="layui-body">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
            <legend>Samples</legend>
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
                    <th>Uploaded By</th>
                    <th>Uploaded At</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${samples}" var="item" varStatus="loop">
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.uploadedBy}</td>
                        <td>${item.createdAt}</td>
                        <td><a href="matching?sampleId=${item.id}"><button type="button" class="layui-btn layui-btn-sm layui-btn-normal">matching</button></a></td>
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
    //JavaScript代码区域
    layui.use('element', function(){
        var element = layui.element;

    });
</script>
</body>
</html>
