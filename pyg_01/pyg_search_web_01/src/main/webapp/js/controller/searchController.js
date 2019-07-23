//控制层
app.controller('searchController' ,function($scope,$controller   ,searchService) {

    $controller('baseController', {$scope: $scope});//继承
    $scope.searchEntity = {keywords:'华为'};//定义变量保存页面初始时的搜索框的值

    $scope.searchResult = {itemList:[]};//保存查询结果
    $scope.initSearch=function () {
        searchService.initSearch($scope.searchEntity).success(function (res) {
            $scope.searchResult=res
        })
    }
});