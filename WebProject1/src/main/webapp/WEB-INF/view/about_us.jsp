<%--
  Created by IntelliJ IDEA.
  User: caitlynjiang
  Date: 2020/4/29
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">

        <div class="layui-logo">&#12288 &#12288 &#12288 Precision Medicine</div>
        <ul class="layui-nav layui-layout-left">

            <li class="layui-logo">Matching System &#12288 &#12288 &#12288 &#12288</li>
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
                            Dashboard <span class="sr-only"></span>
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
                <li class="layui-nav-item"><a href="<%=request.getContextPath()%>/aboutUs">About Us (current)</a></li>
            </ul>
        </div>
    </div>

    <div class="layui-body">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
            <legend>About Us</legend>
        </fieldset>

        <div class="layui-collapse" lay-accordion="">
            <div class="layui-colla-item">
                <h2 class="layui-colla-title">Group Members</h2>
                <div class="layui-colla-content layui-show">
                    <p>Jeff Gui     &#12288 &#12288 &#12288Email: Yifan.18@intl.zju.edu.cn
                        <br>Jixin Wang     &#12288 &#12288 &#12288Email: Jixin.18@intl.zju.edu.cn
                        <br>Yuxing Zhou     &#12288 &#12288 &#12288Email: Yuxing.18@intl.zju.edu.cn
                        <br>Caiylyn Jiang     &#12288 &#12288 &#12288Email: Anlan.18@intl.zju.edu.cn
                        <br>Valerya Wu     &#12288 &#12288 &#12288Email: Xinyu.18@intl.zju.edu.cn
                    </p>
                </div>
            </div>
            <div class="layui-colla-item">
                <h2 class="layui-colla-title">Our Github Online Repository</h2>
                <div class="layui-colla-content">
                    <p>https://github.com/Jeff-Gui/DST-project-group-2</p>
                </div>
            </div>
            <div class="layui-colla-item">
                <h2 class="layui-colla-title">Task Allocation & Group Log</h2>
                <div class="layui-colla-content">
                    <p>不知道写什么好</p>
                </div>
            </div>
            <div class="layui-colla-item">
                <h2 class="layui-colla-title">⭐️ Give Us Kudos！⭐️</h2>
                <div class="layui-colla-content">
                    <p>不知道写什么好</p>
                </div>
            </div>
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
