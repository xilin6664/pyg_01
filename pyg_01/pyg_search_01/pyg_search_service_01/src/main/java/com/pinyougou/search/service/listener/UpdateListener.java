package com.pinyougou.search.service.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * 更新索引的消息监听器
 */
public class UpdateListener implements MessageListener {
    @Autowired
    private TbItemMapper itemMapper ;
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public void onMessage(Message message) {
        //从消息中获取goodsid
        try {
            String goodsId = ((TextMessage)message).getText();
            //根据goodsid和status查所该商品对应的索引库存数据列表
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(Long.valueOf(goodsId)).andStatusEqualTo("1");
            List<TbItem> tbItemList = itemMapper.selectByExample(example);
            // 循环库存列表数据,将spec规格数据转成对应的map动态域数据,
            for (TbItem tbItem : tbItemList) {
                Map<String, String> map = JSON.parseObject(tbItem.getSpec(),Map.class);
                tbItem.setSpecMap(map);
            }
            //将数据存入索引库
            solrTemplate.saveBeans(tbItemList);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
