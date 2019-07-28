 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	} ;
    $scope.searchEntity={};
	
	//分页
	$scope.findPage=function(page,rows){
       // $scope.searchEntity.status='1';//查询商家以提交未审核的商品信息
        goodsService.search(page,rows,$scope.searchEntity).success(

            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
	};


    //初始化分类数组信息
    $scope.itemCatNames=[];//用来保存分类的数组
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(function (res) {
            for (var i=0;i<res.length;i++){
                $scope.itemCatNames[res[i].id]=res[i].name;
            }
        })
    }
    //定义数组保存商品审核状态
    $scope.statuses=['未提交','待审核','审核通过','已驳回'];

    //商品审核或驳回
	$scope.updateAuditStatus=function (auditStatus) {
        goodsService.updateAuditStatus(auditStatus,$scope.selectIds).success(function (response) {
            alert(response.message);
            if(response.success){
                //重新查询
                $scope.reloadList();//重新加载
            }
        })

    };
	//商品批量删除
	$scope.deleteGoods=function () {
        goodsService.deleteGoods($scope.selectIds).success(function (response) {
            alert(response.message);
            if(response.success){
                //重新查询
                $scope.reloadList();//重新加载
            }
        })
    };
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	};
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
    //生成静态模板 staticPage(goods.id);
	$scope.staticPage=function (goodsId) {
        goodsService.staticPage(goodsId).success(function (res) {
            alert(res.message);//输出提示信息
			if (res.success){
                $scope.reloadList();//刷新列表
			}
        })
    }
});	
