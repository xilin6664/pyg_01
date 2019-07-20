 //控制层 
app.controller('goodsController' ,function($scope,$controller, itemCatService,specificationService, typeTemplateService,uploadService,shopGoodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		shopGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}

	
	//查询实体 
	$scope.findOne=function(id){
		shopGoodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	};
	
	//保存 
	$scope.save=function(){
      alert($scope.entity.tbGoods.brandId);
		//保存商品的描述信息
		$scope.entity.tbGoodsDesc.introduction=editor.html();
		var serviceObject;//服务层对象  				
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=shopGoodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=shopGoodsService.add( $scope.entity  );//增加
		}				
		serviceObject.success(
			function(response){
                alert(response.message);
				if(response.success){
                    //清空变量的值
                    $scope.entity = {tbGoods:{}, tbGoodsDesc:{itemImages:[],specificationItems:[]}, itemList:[]};//复合类变量
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
		shopGoodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){

				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		shopGoodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
    };
    $scope.entity = {tbGoods:{}, tbGoodsDesc:{itemImages:[],specificationItems:[]}, itemList:[{spec:{},price:100,num:9999,status:0,isDefault:0}]};
   // $scope.entity = {tbGoods:{}, tbGoodsDesc:{itemImages:[],specificationItems:[]}, itemList:[]};//复合类变量
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
	$scope.$watch('entity.tbGoods.category1Id',function (newValue,oldValue) {
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
	$scope.$watch('entity.tbGoods.category2Id',function (newValue,oldValue) {
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
    $scope.$watch('entity.tbGoods.category3Id',function (newValue,oldValue) {
        if (undefined != newValue ){//只有变化之后的值不为undefined时才发送请求查询
        //根据newValue查询对应的模板id进行展示
             itemCatService.findOne(newValue).success(function (res) {
            $scope.entity.tbGoods.typeTemplateId = res.typeId;
        })
        }
    });
    $scope.brandList = [];//保存品牌数组
    $scope.specList = [];//保存根据模板查询到规格数据
	//监控模板id的变化动态展示对应的品牌下拉列表
    $scope.$watch('entity.tbGoods.typeTemplateId',function (newValue1,oldValue) {
        if ('' != newValue1 && undefined != newValue1){//只有变化之后的值不为undefined时才发送请求查询
        //根据newValue查询对应的模板id进行展示
        typeTemplateService.findOne(newValue1).success(function (res) {
            $scope.brandList =JSON.parse(res.brandIds);//保存品牌数组
        });
        //根据typetemplateid查询对应的规格数据
            specificationService.findSpecByTypeTemplateId(newValue1).success(function (res) {
                $scope.specList=res;
            })
        }else {
            $scope.specList = [];//初始化数据
		}
    });
    $scope.image={color:'',url:''};//图片对象
	$scope.uploadFile=function () {
		//调用uploadService实现上传
        uploadService.uploadFile().success(function (res) {
			if(res.success){
                //成功回显数据
				$scope.image.url=res.message;
			}else {
				//失败.提示
                alert(res.message);
			}
        })
    };
    //插入一行图片信息
	$scope.insertRow=function () {
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image);
    };
    //删除一行图片信息
    $scope.deleteRow=function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    }

    //动态构建SKU数据选项框   specificationItems:[]
   $scope.selectSpecs=function ($event,specName,optionName) {
    	//根据specname 从specificationItems:[]规格和规格选项数组中查找,是否有数据
	   var specObject = $scope.searchObjectByKeyFromArragy($scope.entity.tbGoodsDesc.specificationItems,specName,'attributeName');
	   //判断是否为空.为空,之前数组中没有对应的规格数据往数组中添加一个对象
	   if(specObject != null) {
           //不为空,判断规格选项是否为选中状态,选中状态,往attributeValue中添加一个规格选项数据
		   if ($event.target.checked){
               specObject.attributeValue.push(optionName);
		   }else {
               //不为空,判断规格选项是否为选中状态,未选中状态,从attributeValue中删除一个规格选项数据
               var index = specObject.attributeValue.indexOf(optionName);
               specObject.attributeValue.splice(index,1);
               //判断specObject.attributeValue数组长度是否为小于等于0(是否为空数组),为空从从specificationItems:[]规格和规格选项数组中删除该规格的数据
			   if (specObject.attributeValue.length <=0){
                   var indexOf = $scope.entity.tbGoodsDesc.specificationItems.indexOf(specObject);
                   $scope.entity.tbGoodsDesc.specificationItems.splice(indexOf, 1);
			   }
           }

       }else {
	   	//未查询到对象,创建对象,将规格和规格选项保存,将对象放入数组中
		   specObject = {attributeName:specName,attributeValue:[optionName]};
           $scope.entity.tbGoodsDesc.specificationItems.push(specObject);//放入数组中
	   }

   };

    //根据选中的规格和规格选项生成库存列表
	$scope.createItemList=function () {
	//初始化库存列表数据
		$scope.entity.itemList=[{spec:{},price:0,num:9999,status:0,isDefault:0}];
		//遍历数组$scope.entity.tbGoodsDesc.specificationItems,获取一个对象
		for (var i =0;i<$scope.entity.tbGoodsDesc.specificationItems.length;i++){
			//将获取的对象和entity.itemList数组中所有的对象合并
			//将合并的结果赋值给entity.itemList,下次循环使用
            $scope.entity.itemList = $scope.addColum($scope.entity.tbGoodsDesc.specificationItems[i], $scope.entity.itemList);
		}
    };
	//定义方法 将一条规格和规格选项数据 于 $scope.entity.itemList进行数据合并
    /*$scope.addColum=function (specObject,itemList) {
        //获取规格中的所有选项数据
        var newItemList=[];//用来保存合并之后的库存信息数据
        //specObject-->{attributeName:'网络',attributeValue:[3G,4G]}
        var specOptions = specObject.attributeValue;
        for(var j=0;j<itemList.length;j++){
            var item = itemList[j];//-->{spec:{},price:100,num:999...}

            //将item转换成字符串,再转换成对象
            var newItem = JSON.parse(JSON.stringify(item));
            for (var i =0;i<specOptions.length;i++){
                newItem.spec[specObject.attributeName]=specOptions[i];//拼接:spec:{'网络':3G}
                newItemList.push(newItem);

			}
        }


        return newItemList;
    }*/
    $scope.addColum=function (specObject,itemList) {
    	//获取规格中的所有选项数据
		var newItemList=[];//用来保存合并之后的库存信息数据
		//specObject-->{attributeName:'网络',attributeValue:[3G,4G]}
		var specOptions = specObject.attributeValue;
		for (var i =0;i<specOptions.length;i++){
			for(var j=0;j<itemList.length;j++){
				var item = itemList[j];//-->{spec:{},price:100,num:999...}
				//将item转换成字符串,再转换成对象
                var newItem = JSON.parse(JSON.stringify(item));
                newItem.spec[specObject.attributeName]=specOptions[i];//拼接:spec:{'网络':3G}
                newItemList.push(newItem);//存入新数组中
			}
		}
		return newItemList;
    };
    //初始化规格,选项和和库存信息列表
	$scope.initSpecItemsAndItemList=function () {
        $scope.entity.tbGoodsDesc.specificationItems = [];
        $scope.entity.itemList = [{spec:{},price:100,num:9999,status:0,isDefault:0}];
		
    };
    $scope.searchEntity={};

    //分页
    $scope.findPage=function(page,rows){
        shopGoodsService.search(page,rows,$scope.searchEntity).success(
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
    $scope.statuses=['未提交','审核中','审核通过','已驳回'];
    //定义数组保存商品上下架状态
    $scope.marketableStatus=['下架','上架'];
    //修改商品上下架状态
    $scope.setMarketableStatus=function (marketableStatus) {
        shopGoodsService.setMarketableStatus(marketableStatus,$scope.selectIds).success(function (response) {
            alert(response.message);
            if(response.success){
                //重新查询
                $scope.reloadList();//重新加载
            }
        })
    }
});	
