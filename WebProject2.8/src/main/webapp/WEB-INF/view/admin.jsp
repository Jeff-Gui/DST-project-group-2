<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
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
    <title>Update</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/layui/css/layui.css" media="all">

</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <jsp:include page="nav.jsp" >
        <jsp:param name="active" value="updatePharmgkb" />
    </jsp:include>

    <div class="layui-body">
        <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
            <legend>Control Panel</legend>
        </fieldset>

        <form method="post" action="<%=request.getContextPath()%>/import" >
            <div style="margin-top: 10px; margin-left: 10px">
                <div class="form-group">
                    <label for="update">Update knowledge base data?</label>
                    <input type="radio" class="form-control" name="update" id="update" value="true"/>Yes (may take minutes to complete)
                    <input type="radio" class="form-control" name="update" id="update2" value="false" checked/>No
                </div>
            </div>
            <div style="margin-top: 10px; margin-left: 10px">
                <div class="form-group">
                    <label for="deleteSampleId">Delete samples by id (comma sep)</label>
                    <input type="text" class="form-control" id="deleteSampleId" name="deleteSampleId">
                </div>
            </div>
            <div style="margin-top: 10px; margin-left: 10px">
                <div class="form-group">
                    <label for="deleteUserName">Delete user by username (comma sep)</label>
                    <input type="text" class="form-control" id="deleteUserName" name="deleteUserName">
                </div>
            </div>
            <div style="margin-top: 10px; margin-left: 10px">
                <button type="submit" class="layui-btn" id="test4">Submit changes</button>
            </div>
        </form>

        <br/>
        <div style="margin-top: 10px; margin-left: 10px">
            <h2>Knowledge base metadata</h2>
        </div>
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
                    <th>Category</th>
                    <th>Number of records</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Clinic annotations</td>
                    <td><%=request.getAttribute("count_clinic_all")%></td>
                </tr>
                <tr>
                    <td>Dosing guidelines</td>
                    <td><%=request.getAttribute("count_dosing_all")%></td>
                </tr>
                <tr>
                    <td>Drug labels</td>
                    <td><%=request.getAttribute("count_label_all")%></td>
                </tr>
                <tr>
                    <td>Variant drug annotations</td>
                    <td><%=request.getAttribute("count_varDrug_all")%></td>
                </tr>
                </tbody>
            </table>
        </div>

        <br/>
        <div style="margin-top: 10px; margin-left: 10px">
            <h2>Users</h2>
        </div>
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
                    <th>Username</th>
                    <th>Email</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${users}" var="item" varStatus="loop">
                    <tr>
                        <td>${item.username}</td>
                        <td>${item.email}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <br/>
        <div style="margin-top: 10px; margin-left: 10px">
            <h2>Samples</h2>
        </div>
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
                    <th>Sample Type</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${samples}" var="item" varStatus="loop">
                    <tr>
                        <td>${item.id}</td>
                        <td>${item.uploadedBy}</td>
                        <td>${item.createdAt}</td>
                        <td>${item.sampleType}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

    <div class="layui-footer">
        DST Group 2
    </div>
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
