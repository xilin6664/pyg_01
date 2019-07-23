//服务层
app.service('searchService',function($http){
	    	

	this.initSearch=function(searchEntity){
		return $http.post('../search/search',searchEntity);
	}

});
