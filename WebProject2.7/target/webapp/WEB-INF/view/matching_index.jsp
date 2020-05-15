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
    <title>Matching</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/layui/css/layui.css" media="all">


</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">&#12288 &#12288 &#12288 Precision Medicine</div>
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
                            Dashboard <span class="sr-only"></span>
                        </a></dd>
                        <dd><a href="<%=request.getContextPath()%>/matchingIndex">
                            <span data-feather="file"></span>
                            Matching (current)
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
            <legend>Matching</legend>
        </fieldset>

        <%--        <div style="padding: 5px;">Choose your file type：</div>--%>

        <div class="layui-tab layui-tab-brief" lay-filter="docDemoTabBrief">
            <ul class="layui-tab-title">
                <li class="layui-this">annovar</li>
                <li>vep</li>

            </ul>
            <div class="layui-tab-content">
                <div class="layui-tab-item layui-show">
                    <form method="post" action="upload/annovar" enctype="multipart/form-data">
                        <div class="form-group">
                            <label for="exampleFormControlFile1">annovar output</label>
                            <input type="file" class="form-control-file" id="exampleFormControlFile1" name="file">
                        </div>
                        <div style="margin-top: 5px;"></div>
                        <div class="form-group">
                            <label for="uploaded_by">Uploaded By</label>
                            <input type="text" class="form-control" id="uploaded_by" name="uploaded_by">
                        </div>
                        <div style="margin-top: 10px;"></div>
                        <button type="submit" class="layui-btn" id="test4">Upload</button>
                    </form>
                </div>
                <div class="layui-tab-item">
                    <div class="layui-tab-item layui-show">
                        <form method="post" action="upload/vep" enctype="multipart/form-data">
                            <div class="form-group">
                                <label for="exampleFormControlFile1">vep output</label>
                                <input type="file" class="form-control-file" id="exampleFormControlFile2" name="file">
                            </div>
                            <div style="margin-top: 5px;"></div>
                            <div class="form-group">
                                <label for="uploaded_by">Uploaded By</label>
                                <input type="text" class="form-control" id="uploaded_by2" name="uploaded_by">
                            </div>
                            <div style="margin-top: 10px;"></div>
                            <button type="submit" class="layui-btn" id="test5">Upload</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <div class="layui-footer">
        ⭐ ️DST Group 2 ⭐️
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
