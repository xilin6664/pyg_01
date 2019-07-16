 //控制层 
app.controller('itemCatController' ,function($scope,$controller ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;

                //将字符串转换为select2需要的数组类型的数据
                typeTemplateService.findOne($scope.entity.typeId).success(function (res) {
                   // alert(res.text);
                    $scope.entity.typeId={id:res.id,text:res.text};

                })
			}
		);				
	};
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			//将前台select2插件中选择的数据(一个很长的json字符串)转换成需要的格式的数据(模板的id值)
            $scope.entity.typeId= $scope.entity.typeId.id;
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
            $scope.entity.parentId=$scope.parentId;
            //将前台select2插件中选择的数据(一个很长的json字符串)转换成需要的格式的数据(模板的id值)
            $scope.entity.typeId= $scope.entity.typeId.id;
			serviceObject=itemCatService.add( $scope.entity  );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 刷新页面
                    itemCatService.findByParentId($scope.parentId).success(function (res) {
                        $scope.categoryList=res;

                    })
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
                    //重新查询 刷新页面
                    itemCatService.findByParentId($scope.parentId).success(function (res) {
                        $scope.categoryList=res;

                    });
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
    $scope.categoryList = [];//保存商品分类查询结果列表
      //页面初始化的时候查询一级分类列表
    $scope.findCategoryList=function () {
		//parentId==0;
		itemCatService.findByParentId(0).success(function (res) {
            $scope.categoryList=res;

        })
    };
    //定义变量保存分类的级别
    $scope.grade =1;
    $scope.setGrade=function(grade){
        $scope.grade=grade;
	};
    $scope.itemCat1=null;//记录点击的一级分类对象
    $scope.itemCat2=null;//记录点击的二级分类对象
    $scope.parentGradeShow='顶级分类';//保存当前新增级别的上级分类名
    //根据一个分类查询对应的下一级分类列表
    $scope.searchNext=function (category) {
    	if($scope.grade == 1){
    		//grade == 1  $scope.itemCat1=null; $scope.itemCat2=null;
            $scope.itemCat1=null;
            $scope.itemCat2=null;
    		//grade == 1  $scope.parentGradeShow='顶级分类';
            $scope.parentGradeShow='顶级分类';
    		//grade == 1  $scope.parentId=0;
            $scope.parentId=0;
		}else if($scope.grade == 2){
            //grade == 2  $scope.itemCat1=null; $scope.itemCat2=null;
            $scope.itemCat1=category;
            $scope.itemCat2=null;
            //grade == 2  $scope.parentGradeShow=category.name;
            $scope.parentGradeShow=category.name;
            //grade == 2  $scope.parentId=category.id;
            $scope.parentId=category.id;
        }else if($scope.grade == 3){
            //grade == 3   $scope.itemCat2=category;
            $scope.itemCat2=category;
            //grade == 3  $scope.parentGradeShow='顶级分类';
            $scope.parentGradeShow=$scope.itemCat1.name +'  >>  '+category.name;
            //grade == 3  $scope.parentId=category.id;
            $scope.parentId=category.id;
        }
    	//根据一个分类查询对应的下一级分类列表
        itemCatService.findByParentId($scope.parentId).success(function (res) {
            $scope.categoryList=res;

        })
    };
    //查询所有的模板列表数据,返回并放到指定的变量上
    $scope.templateList = {data: []};
    $scope.findTemplateList=function () {
        typeTemplateService.findAll().success(function (res) {
            $scope.templateList.data=res;//构造select2插件需要的数据
        })

    };
});	
