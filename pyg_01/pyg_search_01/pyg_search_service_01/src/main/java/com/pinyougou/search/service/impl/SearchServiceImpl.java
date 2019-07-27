package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 一般查询 new SimpleQuery();
     * 高亮查询  new SimpleHighlightQuery();
     *
     */
    @Override
    public Map search(Map searchEntity) {
        //根据条件查询
        SimpleQuery groupQuery = new SimpleQuery();//一般查询
        HighlightQuery query = new SimpleHighlightQuery();//高亮查询
        Criteria criteria = new Criteria("item_keywords").is(searchEntity.get("keywords"));
        query.addCriteria(criteria);
        //分类查询
        groupQuery.addCriteria(criteria);//设置查询 条件和高亮条件一致,保证查询结果是一致的
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");//设置分组的域名
        groupQuery.setGroupOptions(groupOptions);
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(groupQuery, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");//获取域名分组结果
        Page<GroupEntry<TbItem>> entries = groupResult.getGroupEntries();
        List<GroupEntry<TbItem>> content = entries.getContent();
        List<String> categoryList = new ArrayList<>();
        for (GroupEntry<TbItem> groupEntry : content) {
            categoryList.add(groupEntry.getGroupValue());//将分类名称添加到数组中
        }

        //高亮显示查询的关键字
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");//设置需要高亮显示的域名
        highlightOptions.setSimplePrefix("<font style='color:red'>");//设置高亮显示标签的前缀
        highlightOptions.setSimplePostfix("</font>");//设置高亮显示标签的后缀
        query.setHighlightOptions(highlightOptions);
        //一般分页查询数据
        //ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        //添加过滤条件查询
        SimpleFilterQuery filterQuery = new SimpleFilterQuery();
        //获取过滤条件,判断是否存在
        String category = (String) searchEntity.get("category");//类别条件过滤查询
        if (StringUtils.isNotBlank(category)) {
            //有值.添加过滤查询条件
            filterQuery.addCriteria(new Criteria("item_category").is(category));
        }
        String brand = (String) searchEntity.get("brand");//品牌条件过滤查询
        if (StringUtils.isNotBlank(brand)) {
            //有值.添加过滤查询条件
            filterQuery.addCriteria(new Criteria("item_brand").is(brand));
        }
        String price = (String) searchEntity.get("price");
        if (StringUtils.isNotBlank(price)) {//非空判断
            //添加价格查询过滤条件
            //将价格字符串 100-500 分割开 ,如果有*则是 3000以上
            String[] strArr = price.split("-");
            if (strArr[1].equals("*")) {//3000以上
                filterQuery.addCriteria(new Criteria("item_price").greaterThanEqual(strArr[0]));
            }else {
                //是一个价格区间
                filterQuery.addCriteria(new Criteria("item_price").between(strArr[0],strArr[1]));
            }
        }
        //规格过滤查询
        Map<String, String> spec = (Map<String, String>) searchEntity.get("spec");
        if (null != spec && spec.keySet().size() > 0) {//非空判断
            for (String key : spec.keySet()) {
                filterQuery.addCriteria(new Criteria("item_spec_" + key).is(spec.get(key)));//添加规格过滤查询条件
            }
        }
        //添加排序条件
        String sort = (String) searchEntity.get("sort");
        String sortField = (String) searchEntity.get("sortField");
        //判断是否有排序条件
        if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(sortField)) {
            //判断升序还是降序
            if ("ASC".equals(sort)) {
                query.addSort(new Sort(Sort.Direction.ASC, sortField));//升序排序
            }else {
                query.addSort(new Sort(Sort.Direction.DESC, sortField));//降序排序
            }
        }
        query.addFilterQuery(filterQuery);//添加查询过滤条件
        //设置分页参数
        Object pageNumber = searchEntity.get("page");
        Integer page =1;
        if (pageNumber instanceof Integer ){//是Integer类型
             page = (Integer) pageNumber;//当前页
        }else if(pageNumber instanceof String){//是String类型
             page = Integer.parseInt(pageNumber.toString());
        }
        Integer size = (Integer) searchEntity.get("size");//每页显示条数
        //判断分页参数的合法性
        if (null == page || page <= 0) {
            page =1;//默认第一页
        }
        if (null == size || size <= 0) {
            size =5;//默认一页五条数据
        }
        //将参数设置到查询条件中
        query.setOffset((page - 1) * size);//起始数据
        query.setRows(size);//每页显示的条数
        //高亮分页查询
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();//获取Highlighted对应的集合数据
        for (HighlightEntry<TbItem> entry : highlighted) {
            TbItem entity = entry.getEntity();//高亮对应的原始item对象
            //判断高亮数据是否存在
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();//获取高亮数据数组
            if (highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets().size() >0) {
           //将高亮数据设置到要显示的数据中
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        Map map = new HashMap();//封装返回的数据
        //品牌列表 和规格选项数据查询
        if (categoryList.size() > 0) {
            //品牌列表
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(categoryList.get(0));//默认显示第一个分类对应的品牌列表
            map.put("brandList",brandList);
            //规格列表数据
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(categoryList.get(0));//默认第一个分类对应的规格数据
            map.put("specList", specList);
        }
        map.put("total", tbItems.getTotalElements());//总条数
        map.put("totalPages", tbItems.getTotalPages());//总页数
        map.put("itemList", tbItems.getContent());//每页显示的数据
        map.put("categoryList", categoryList);//显示分类数据
        return map;
    }
}
