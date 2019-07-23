package com.pinyougou.dataimport.service;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 将MySQL数据库中的信息导入到solr搜索引擎中
 */
@Component
public class DataImportService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    //定义方法,将数据导入到solr中
    public void importDataFromDB2Solr(){
        //查询商品库存表中有效的商品信息(status ==1)
        TbItemExample example = new TbItemExample();
        example.createCriteria().andStatusEqualTo("1");
        List<TbItem> list = itemMapper.selectByExample(example);
        //将数据保存到索引库中
        solrTemplate.saveBeans(list);//保存数据到索引库
        solrTemplate.commit();//提交事务
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportService dataImportService = (DataImportService) ac.getBean("dataImportService");
        dataImportService.importDataFromDB2Solr();
    }
}
