app.controller('indexController',function ($scope,$http,indexService,$controller) {
    $controller('baseController', {$scope:$scope});//继承baseController,将父模块中的$scope赋值给子模块的$scope
    $scope.username = '';//当前登录的用户名
    $scope.findLoginUser=function(){
        indexService.findLoginUser().success(function (res) {
            $scope.username= res.username;
        })
    };

});