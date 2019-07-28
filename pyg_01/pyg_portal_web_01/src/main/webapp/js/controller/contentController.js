 //控制层 
app.controller('contentController' ,function($scope,$controller   ,contentService,contentCategoryService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		contentService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		contentService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		contentService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=contentService.update( $scope.entity ); //修改  
		}else{
			serviceObject=contentService.add( $scope.entity  );//增加 
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
		contentService.dele( $scope.selectIds ).success(
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
		contentService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	$scope.contentCategoryList=[];//保存广告分类数据
    //查询所有广告分类信息
	$scope.findContentCategoryList=function () {
        contentCategoryService.findAll().success(function (res) {
            $scope.contentCategoryList=res;
        })
    };
    $scope.entity={};//前台保存广告内容的对象
    //上传广告图片文件
    $scope.uploadFile=function () {
        uploadService.uploadFile().success(function (res) {
            if (res.success){
                //回显图片
                $scope.entity.pic=res.message;
            }else {
                //提示失败
                alert(res.message);
            }
        })
    };
    $scope.contentList=[];//保存轮播广告数据
    //根据categoryId查询轮播图片集合
	$scope.findContentList=function (catId) {
		contentService.findContentList(catId).success(function (res) {
            $scope.contentList=res;
        })
    }
    //点击搜索按钮时完成跳转
    $scope.jumpToSearch = function () {
        if($scope.keywords == undefined || $scope.keywords == ''){
            $scope.keywords = '华为';

        }
        location.href='http://search.pinyougou.com/search.html#?keywords='+$scope.keywords;
    }
});	
