 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService, itemCatService, typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		//保存商品的描述信息
		$scope.entity.tbGoodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    //清空变量的值
                    $scope.entity = {tbGoods:{}, tbGoodsDesc:{}, itemList:[]};//复合类变量
                    $scope.itemCat1List=[];//一级分类列表
                    $scope.itemCat2List = [];//二级分类列表
                    $scope.itemCat3List = [];//三级分类列表
                    $scope.entity.tbGoods.typeTemplateId = '';//模板id
                    $scope.brandList = [];
                    editor.html('');
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
    $scope.entity = {tbGoods:{}, tbGoodsDesc:{}, itemList:[]};//复合类变量
    $scope.itemCat1List=[];//一级分类列表
    $scope.itemCat2List = [];//二级分类列表
    $scope.itemCat3List = [];//三级分类列表
    $scope.entity.tbGoods.typeTemplateId = '';//模板id
    //页面初始化,获取一级分类列表
	$scope.findCategory1List=function (parentId) {
        itemCatService.findByParentId(parentId).success(function (res) {
            $scope.itemCat1List=res;
          /*  //清空之前的数据
            $scope.itemCat2List = [];//二级分类列表
            $scope.itemCat3List = [];//三级分类列表
            $scope.entity.tbGoods.typeTemplateId = '';//模板id
            $scope.brandList = [];//保存品牌数组*/
        })
    }
/*
watch监控函数,可以监控变量或函数的返回值变化,在变化时触发指定的函数.
	参数1,要监控的变量或是要监控的函数
	参数2,在变化时要调用的函数.该函数有两个参数:1,变化之后的值.2,变化之前的值
 */
    //监控一级分类选项变化,构建对应的二级列表
	$scope.$watch('entity.TbGoods.category1Id',function (newValue,oldValue) {
		if (undefined != newValue){//只有变化之后的值不为undefined时才发送请求查询对应的二级分类
			//根据newValue查询对应的下级分类.展示到二级分类下拉框
            itemCatService.findByParentId(newValue).success(function (res) {
                $scope.itemCat2List =res;
                //清空之前的数据
                $scope.itemCat3List = [];//三级分类列表
                $scope.entity.tbGoods.typeTemplateId = '';//模板id
                $scope.brandList = [];//保存品牌数组
            })
			
		}
    });
	//监控二级分类的变化动态构建三级分类列表
	$scope.$watch('entity.TbGoods.category2Id',function (newValue,oldValue) {
        if (undefined != newValue){//只有变化之后的值不为undefined时才发送请求查询
		//根据newValue查询对应的三级分类列表并展示
        itemCatService.findByParentId(newValue).success(function (res) {
            $scope.itemCat3List =res;
            //清空之前的数据
            $scope.entity.tbGoods.typeTemplateId = '';//模板id
            $scope.brandList = [];//保存品牌数组
        })}
    });
	//监控三级分类的变化动态查询到对应的模板id进行展示
    $scope.$watch('entity.TbGoods.category3Id',function (newValue,oldValue) {
        if (undefined != newValue ){//只有变化之后的值不为undefined时才发送请求查询
        //根据newValue查询对应的模板id进行展示
             itemCatService.findOne(newValue).success(function (res) {
            $scope.entity.tbGoods.typeTemplateId = res.typeId;
        })
        }
    });
    $scope.brandList = [];//保存品牌数组
	//监控模板id的变化动态展示对应的品牌下拉列表
    $scope.$watch('entity.tbGoods.typeTemplateId',function (newValue1,oldValue) {
        if ('' != newValue1){//只有变化之后的值不为undefined时才发送请求查询
        //根据newValue查询对应的模板id进行展示
        typeTemplateService.findOne(newValue1).success(function (res) {
            $scope.brandList =JSON.parse(res.brandIds);//保存品牌数组
        })}
    });
});	
