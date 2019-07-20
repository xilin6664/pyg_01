app.service('specificationService',function ($http) {
    //查询全部
    this.findAll=function () {
        return $http.get('../specification/findAll');
    };
    //保存,参数是页面数据封装的对象
    this.save=function (entity) {
        return $http.post('../specification/save',entity);
    };
    //分页查询
    this.findPage=function (page,size) {
        return $http.get('../specification/findPage/'+page+"/"+size);
    };
    //根据id查询
    this.findOne=function (id) {
        return $http.get('../specification/findOne/'+id);
    };
    //根据选中的id删除
    this.delete=function (selectIds) {
        return $http.get('../specification/delete/'+selectIds);
    }
    //根据typetemplateid查询对应的规格数据
    this.findSpecByTypeTemplateId=function (typeTemplateId) {
        return $http.get('../specification/findSpecByTypeTemplateId/'+typeTemplateId);
    }
});