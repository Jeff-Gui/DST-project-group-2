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
    <jsp:include page="nav.jsp" >
        <jsp:param name="active" value="matching" />
    </jsp:include>

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
                            <label for="description">Description</label>
                            <input type="text" class="form-control" id="description" name="description">
                        </div>
                        <div class="form-group">
                            <label for="publicity">Allow other users to view this sample? </label>
                            <input type="radio" class="form-control" name="publicity" id="publicity" value="true"/> Yes
                            <input type="radio" class="form-control" name="publicity" id="publicity2" value="false" checked/> No
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
                                <label for="description">Description</label>
                                <input type="text" class="form-control" id="description2" name="description">
                            </div>
                            <div class="form-group">
                                <label for="publicity">Allow other users to view this sample? </label>
                                <input type="radio" class="form-control" name="publicity" id="publicity3" value="true"/> Yes
                                <input type="radio" class="form-control" name="publicity" id="publicity4" value="false" checked/> No
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
