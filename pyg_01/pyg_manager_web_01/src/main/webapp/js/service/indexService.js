app.service('indexService',function ($http) {
    //查询全部
    this.findLoginUser=function () {
        return $http.get('../index/findLoginUser');
    };

});