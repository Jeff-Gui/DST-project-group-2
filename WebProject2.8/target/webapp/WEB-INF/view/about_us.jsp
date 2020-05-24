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
    <title>About Us DST-Group-2</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet"  href="static/layui/css/layui.css"  media="all">


</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <jsp:include page="nav.jsp" >
        <jsp:param name="active" value="aboutUs" />
    </jsp:include>

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
                    <p><a href="https://github.com/Jeff-Gui/DST-project-group-2">https://github.com/Jeff-Gui/DST-project-group-2</a></p>
                    <button type="button" class="layui-btn layui-btn-sm"><a href="https://github.com/Jeff-Gui/DST-project-group-2">Go to Repository</a></button>
                </div>
            </div>
            <div class="layui-colla-item">
                <h2 class="layui-colla-title">Task Allocation & Group Log</h2>
                <div class="layui-colla-content">
                    <p>Empty</p>
                </div>
            </div>
            <div class="layui-colla-item">
                <h2 class="layui-colla-title">⭐️ Give Us Kudos！⭐️</h2>
                <div class="layui-colla-content">
                    <p>Empty</p>
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
    layui.use('element', function(){
        var element = layui.element;

    });
</script>
</body>
</html>
