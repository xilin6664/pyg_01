app.controller('baseController', function ($scope ) {
    // 分页控件配置
    // currentPage：当前页；totalItems：总记录数；itemsPerPage：每页记录数；perPageOptions：分页选项；onChange：当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    //重新加载列表 数据
    $scope.reloadList = function () {
        //切换页码
        $scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };
    $scope.selectIds=[];//保存选中的id的数组
    $scope.selectOptions=function ($event,id) {
        //判断是否选中
        if ($event.target.checked) {
            //选中的将id存入数组中
            $scope.selectIds.push(id);
        }else {
            //没选中的从数组中将id删除
            var index = $scope.selectIds.indexOf(id);//获取id在数组中的索引
            $scope.selectIds.splice(index, 1);//参数1:从哪个索引开始删除.参数2,删除几个元素
        }
    };
    //将json字符串转换成json数组,遍历获取值拼接的方法
    $scope.jsonToString=function (jsonStr,key) {
        //parse将json字符串转换成数组
        var jsonArr = JSON.parse(jsonStr);
        //遍历数组,根据key获取值拼接
        var arr = new Array();//定义变量用来存放需要的数据
        for (var i=0;i<jsonArr.length;i++) {
            arr.push(jsonArr[i][key]);//根据给定的key的值从数组元素获取对应的值,并存入临时数组中
        }
        // 返回拼接的结果数据:arr.join(",")将数组中的元素以逗号分割拼接成字符串
        return arr.join(",");
    }
});