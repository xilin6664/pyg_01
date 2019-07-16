<%--
  Created by IntelliJ IDEA.
  User: lixilin
  Date: 2019/7/14
  Time: 17:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>select2-多选下拉框</title>
    <link rel="stylesheet" href="../../plugins/select2/select2.css" />
    <link rel="stylesheet" href="../../plugins/select2/select2-bootstrap.css"/>
    <script src="../../plugins/jQuery/jquery-2.2.3.min.js"></script>
    <script src="../../plugins/angularjs/angular.min.js"></script>
    <script src="../../plugins/select2/select2.min.js"></script>

    <script>
        var app = angular.module('app', []);
        app.controller('testController',function ($scope) {
            $scope.brandList={data:[{id:'1',text:'联想'},{id:'2',text:'华为'},{id:'3',text:'小米'}]};

        })
    </script>
    <script src="../../plugins/select2/angular-select2.js"></script>
</head>
<body ng-app="app" ng-controller="testController">
  <input select2 select2-model="entity.brandIds" config="brandList" multiple placeholder="选择品牌">

</body>
</html>
