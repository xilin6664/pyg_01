//服务层
app.service('goodsService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../goods/findAll');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('../goods/findPage/'+page+'/'+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('../goods/findOne/'+id);
	}
	//增加 
	this.add=function(entity){
		return  $http.post('../goods/add',entity );
	}
	//修改 
	this.update=function(entity){
		return  $http.post('../goods/update',entity );
	}
	//删除
	this.dele=function(ids){
		return $http.get('../goods/delete/'+ids);
	};
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('../goods/search/'+page+"/"+rows, searchEntity);
	} ;
	//批量商品审核或驳回  updateAuditStatus
    this.updateAuditStatus=function(auditStatus,selectIds){
        return $http.get('../goods/updateAuditStatus/'+auditStatus+'/'+selectIds);
    }
    //商品批量删除  deleteGoods
    this.deleteGoods=function(selectIds){
        return $http.get('../goods/deleteGoods/'+selectIds);
    };
    //商品生成静态页面
	this.staticPage=function (goodsId) {
        return $http.get('../goods/geneItemHtml/'+goodsId);
    }
    //全部商品生成静态页面
    this.goodsHtmlAll=function () {
        return $http.get('../goods/goodsHtmlAll');
    }

});
