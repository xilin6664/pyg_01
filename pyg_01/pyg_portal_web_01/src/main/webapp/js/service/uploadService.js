//服务层
app.service('uploadService',function($http){
    this.uploadFile=function(){
        //上传
        //将文件数据放到表单对象中formdata就是xmlhttprequest level2
        //新增的一个对象.利用他来提交表单模拟表单提交.当然最大的优势就是可以上传二进制文件
        var formData=new FormData();
        formData.append('file',file.files[0]);
        return $http({
			method:'post',
			url:'../upload/uploadFile',
			data:formData,
			headers:{'Content-type':undefined},//固定写法,用来传递二进制文件
			transformRequest:angular.identity
		});
	}

});
