 //控制层 
app.controller('itemController' ,function($scope,$controller   ,itemService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //判断规格选中 isSelected('${specItem.attributeName}', '${optionName}')
	$scope.isSelected=function(key,value){
		if(spec[key] == value){
			return true;
		}
		return false;
	}    
	
//点击切换规格  selectAndJump('${specItem.attributeName}','${optionName}')"
    $scope.selectAndJump = function(key, value) {
		spec[key]=value;//点击的规格选项数据: 网络 -- 联通3G
		//循环所有的规格选项数据
		for(var i =0; i<itemList.length;i++){
		if($scope.hasMatchObj(itemList[i].spec,spec)){
			//找到数组中与选择的规格选项相等的规格数据,完成页面跳转
            location.href = itemList[i].id + '.html';
		}
		}
    }
$scope.hasMatchObj=function (map1,map2) {
	for(key in map1){
		if(map1[key] != map2[key]){//只要有元素的值不相等
			return false;//返回false
		}
	}
	//循环结束都相等
	return true;//返回true
}
//购买数量更新
	$scope.updateGoodNum=function (goodsNum) {
		$scope.num = $scope.num + goodsNum;
		if ($scope.num < 1){
            $scope.num = 1;
		}
    }
});	
