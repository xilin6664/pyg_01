package com.pinyougou.dataimport.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
        //循环商品列表,将spec转换成map,放到动态映射属性specmap
        for (TbItem tbItem : list) {
            //必须指定泛型.不然会报空指针
            Map<String, String> map = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);
        }
        //将数据保存到索引库中
        solrTemplate.saveBeans(list);//保存数据到索引库
        solrTemplate.commit();//提交事务
    }
    /**
     *定义方法将品牌规格等数据导入redis缓存中
     *查询所有的商品分类.根据分类对应的模板获取品牌,规格和扩展属性值,以分类名为key存入redis中
     */
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    public void importDataFromDB2Redis(){
        //查询所有分类信息
        List<TbItemCat> itemList = itemCatMapper.selectByExample(null);
        for (TbItemCat itemCat : itemList) {
            //根据分类信息的typeid查询对应的模板信息
            /*
            itemCat.getTypeId()-->从json中获取到的值默认是int类型.需要先转成String在转成long类型
             */
            TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(Long.valueOf(String.valueOf(itemCat.getTypeId())));
            //将模板的品牌以分类名为key放入redis中
            List<Map> brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(itemCat.getName(),brandList);
            //将模板的规格以分类名为key放入redis中
            List<Map> specList = JSON.parseArray(tbTypeTemplate.getSpecIds(), Map.class);
            for (Map map : specList) {
                //根据规格id查询到对应的规格选项,放入规格map中, key= options
                TbSpecificationOptionExample example= new TbSpecificationOptionExample();
                example.createCriteria().andSpecIdEqualTo(Long.parseLong(String.valueOf(map.get("id"))));
                //获取规格对应的规格选项
                List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
                map.put("options", options);//放人规格数据中
            }
            //设置分类对应的规格数据
            redisTemplate.boundHashOps("specList").put(itemCat.getName(),specList);
            //将模板对应的扩展属性以分类为key 放入redis中
            List<Map> custAttrList = JSON.parseArray(tbTypeTemplate.getCustomAttributeItems(), Map.class);
            redisTemplate.boundHashOps("cusAttrList").put(itemCat.getName(),custAttrList);
        }

    }
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportService dataImportService = (DataImportService) ac.getBean("dataImportService");
        dataImportService.importDataFromDB2Solr();//导入数据到solr
       // dataImportService.importDataFromDB2Redis();//导入数据到redis
    }
}
