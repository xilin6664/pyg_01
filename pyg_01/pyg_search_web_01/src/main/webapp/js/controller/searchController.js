//控制层
app.controller('searchController' ,function($scope,$controller   ,searchService) {

    $controller('baseController', {$scope: $scope});//继承
    $scope.searchEntity = {keywords:'华为',spec:{},price:'',brand:'',category:'',sortField:'item_price',sort:'ASC',
        page:1,size:5};//定义变量保存页面初始时的搜索框的值

    $scope.searchResult = {itemList:[],categoryList:[],specList:[],brandList:[]};//保存查询结果
    $scope.initSearch=function () {
        $scope.search();//调用搜索方法
    };
    //添加搜索查询条件方法 addSearchMap
    $scope.addSearchMap=function (key,value) {
        //从对象中根据key设置对应的值
        $scope.searchEntity[key]=value;
        //重新搜索
        $scope.search();
    };
    //规格条件过滤  将规格和规格名称加入到search entity.spec对象中
    $scope.addSearchMapSpec=function (specName,optionName) {
        $scope.searchEntity.spec[specName]=optionName;
        //重新搜索
        $scope.search();
    };
//点击页面条件中的×时将对应的查询条件删除
    $scope.deleteSearchEntity=function (key) {
        $scope.searchEntity[key]='';//清空查询条件
        //重新搜索
        $scope.search();
    };
    //清空规格查询条件 从查询条件对象中删除规格,和对应的规格选项
    $scope.deleteSpecSearchEntity=function (key) {
        //删除 key -- value 类型的对象
        delete $scope.searchEntity.spec[key];
        //重新搜索
        $scope.search();
    };
//记录排序方式和排序的域名
    $scope.addSortField=function (sortField) {
        $scope.searchEntity.sortField=sortField;
        //更换排序原来的方式
        if ('ASC' == $scope.searchEntity.sort) {
            //如果是升序改为降序
            $scope.searchEntity.sort = 'DESC';
        }else {
            $scope.searchEntity.sort = 'ASC';
        }
        //重新搜索
        $scope.search();
    };
    //定义搜索方法
    $scope.search=function () {
        searchService.initSearch($scope.searchEntity).success(function (res) {
            $scope.searchResult=res;
            $scope.totalPages = res.totalPages;//获取总页数
            $scope.buildPageNums();

        })
    };
    $scope.pageNums = [];//保存要显示的分页的数字编号
    $scope.firstDotShow = true;//控制第一个...的显示或隐藏
    $scope.lastDotShow = true;//控制最后...的显示或隐藏
    $scope.buildPageNums= function () {
        var startNum = 0;//记录第一页数
        var endNum = 0;//记录最后一页数
        if ($scope.totalPages <=5){
            //如果总页数 <=5 startNum=1, endNum=总页数
            startNum=1;
            endNum=$scope.totalPages;
            $scope.firstDotShow = false;//控制第一个...的显示或隐藏
            $scope.lastDotShow = false;//控制最后...的显示或隐藏
        }else {
            //如果总页数 >5  当前页 <=3 :startNum=1, endNum=5
            if ($scope.searchEntity.page <= 3){
                startNum=1;
                endNum=5;
                $scope.firstDotShow = false;//控制第一个...的显示或隐藏
                $scope.lastDotShow = true;//控制最后...的显示或隐藏
            }else if($scope.searchEntity.page < $scope.totalPages-2 ){
                //如果总页数 >5  当前页 >3 && 当前页< 总页数-2  :startNum=当前页-2, endNum=当前页+2
                startNum=$scope.searchEntity.page-2;
                endNum=$scope.searchEntity.page+2;
                $scope.firstDotShow = true;//控制第一个...的显示或隐藏
                $scope.lastDotShow = true;//控制最后...的显示或隐藏
            }else {
                //如果总页数 >5  当前页 >= 总页数-2  :startNum=总页数-4, endNum=总页数
                startNum=$scope.totalPages-4;
                endNum=$scope.totalPages;
                $scope.firstDotShow = true;//控制第一个...的显示或隐藏
                $scope.lastDotShow = false;//控制最后...的显示或隐藏
            }
        }
        var  index=0;
        for(var i=startNum;i<=endNum;i++){
            $scope.pageNums[index++]=i;
        }
    };
    //页面跳转到指定的页  searchCurrentPage(num)
    $scope.searchCurrentPage=function(num){
        $scope.searchEntity.page=num;
        //重新搜索
        $scope.search();
    };
    //点击下一页
    $scope.nextPageQuery=function () {
        $scope.searchEntity.page=$scope.searchEntity.page+1;
        //重新搜索
        $scope.search();
    };
    //点击上一页
    $scope.prePageQuery=function () {
        $scope.searchEntity.page=$scope.searchEntity.page-1;
        //重新搜索
        $scope.search();
    };


});