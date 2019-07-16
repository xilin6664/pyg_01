app.controller('specificationController',function ($scope,$http,specificationService,$controller) {
    $controller('baseController', {$scope:$scope});//继承baseController,将父模块中的$scope赋值给子模块的$scope
    //查询全部
    $scope.findAll=function(){
        specificationService.findAll().success(function (res) {
            $scope.specificationList = res;
        })
    };
    //保存
    $scope.entity = {spec:{}, optionList:[]};//对应后台接受的实体类
    $scope.save = function () {
        //发送保存请求，接受返回值并重新刷新界面

        specificationService.save($scope.entity).success(function (res) {
            //提示客户
            alert(res.message);
            //如果成功，刷新界面
            if(res.success){
                $scope.reloadList();
            }
        })
    };
    //分页查询
    $scope.findPage=function(page,size){
        specificationService.findPage(page,size).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    };
    /*根据id查询品牌*/
    $scope.findOne=function (id) {
        specificationService.findOne(id).success(function (res) {
            $scope.entity =res;
        })
    };
    //删除选中的品牌
    $scope.delete=function () {
        specificationService.delete($scope.selectIds).success(function (res) {
            alert(res.message);
            //如果成功刷新界面.清空数组中的元素
            if(res.success) {
                $scope.reloadList();
                $scope.selectIds=[];

            }
        })
    };
    //给规格从表内容添加一行空的行
    $scope.insertRow=function () {
        $scope.entity.optionList.push({});
    };
    //给规格从表内容删除一行
    $scope.deleteRow=function (option) {
        var index = $scope.entity.optionList.indexOf(option);
        $scope.entity.optionList.splice(index,1);//删除一行
    };
});