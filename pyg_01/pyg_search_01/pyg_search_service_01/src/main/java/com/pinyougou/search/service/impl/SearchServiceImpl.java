package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Override //searchEntity = {keywords:
    public Map search(Map searchEntity) {
        //根据条件查询
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchEntity.get("keywords"));
        query.addCriteria(criteria);
        //分页查询数据
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        Map map = new HashMap();//封装返回的数据
        map.put("total", tbItems.getTotalElements());//总条数
        map.put("itemList", tbItems.getContent());//每页显示的数据
        return map;
    }
}
