app.service('brandService',function ($http) {
    //查询全部
    this.findAll=function () {
        return $http.get('../brand/findAll');
    };
    //保存,参数是页面数据封装的对象
    this.save=function (entity) {
        return $http.post('../brand/save',entity);
    };
    //分页查询
    this.findPage=function (page,size) {
        return $http.get('../brand/findPage/'+page+"/"+size);
    };
    //根据id查询
    this.findOne=function (id) {
        return $http.get('../brand/findOne/'+id);
    };
    //根据选中的id删除
    this.delete=function (selectIds) {
        return $http.get('../brand/delete/'+selectIds);
    }
});