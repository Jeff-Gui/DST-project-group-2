<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<%--
  Created by IntelliJ IDEA.
  User: surface
  Date: 2020/5/23
  Time: 23:30
  To change this template use File | Settings | File Templates.
--%>
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
                        ${param.active=="dashboard" ? ">> " : ""}Dashboard <span class="sr-only"></span>
                    </a></dd>
                    <dd><a href="<%=request.getContextPath()%>/matchingIndex">
                        <span data-feather="file"></span>
                        ${param.active=="matching" ? ">> " : ""}Matching
                    </a></dd>
                    <dd><a href="<%=request.getContextPath()%>/samples">
                        <span data-feather="file"></span>
                        ${param.active=="samples" ? ">> " : ""}Samples
                    </a></dd>
                </dl>
            </li>
            <li class="layui-nav-item ${"drugs drugLabels dosingGuideline clinicAnnotation".indexOf(param.active)>=0 ? "layui-nav-itemed" : ""}">
                <a href="javascript:;">Knowledge Base</a>
                <dl class="layui-nav-child">
                    <dd><a href="<%=request.getContextPath()%>/drugs">
                        <span data-feather="file-text"></span>
                        ${param.active=="drugs" ? ">> " : ""}Drugs
                    </a></dd>
                    <dd><a href="<%=request.getContextPath()%>/drugLabels">
                        <span data-feather="file-text"></span>
                        ${param.active=="drugLabels" ? ">> " : ""}Drug Labels
                    </a></dd>
                    <dd><a href="<%=request.getContextPath()%>/dosingGuideline">
                        <span data-feather="file-text"></span>
                        ${param.active=="dosingGuideline" ? ">> " : ""}Dosing Guideline
                    </a></dd>
                    <dd><a href="<%=request.getContextPath()%>/clinicAnn">
                        <span data-feather="file-text"></span>
                        ${param.active=="clinicAnnotation" ? ">> " : ""}Clinic Annotations
                    </a></dd>
                </dl>
            </li>
            <% Object username = session.getAttribute("username");%>
            <% if (username!=null&&username.equals("zju")) { %>
            <li class="layui-nav-item ${"updatePharmgkb" == param.active ? "layui-nav-itemed" : ""}">
                <a href="javascript:">Website Managing</a>
                <dl class="layui-nav-child">
                    <dd><a href="<%=request.getContextPath()%>/reset">
                        <span data-feather="file-text"></span>
                        Reset user password
                    </a></dd>
                </dl>
                <dl class="layui-nav-child">
                    <dd><a href="<%=request.getContextPath()%>/panel">
                        <span data-feather="file-text"></span>
                        ${param.active=="updatePharmgkb" ? ">> " : ""}Update Pharmgkb data
                    </a></dd>
                </dl>
            </li>
            <% } %>
            <li class="layui-nav-item"><a href="">Reload</a></li>
            <li class="layui-nav-item ${"aboutUs" == param.active ? "layui-nav-itemed" : ""}"><a href="<%=request.getContextPath()%>/aboutUs">About Us</a></li>
        </ul>
    </div>
</div>