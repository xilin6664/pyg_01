package com.pinyougou.search.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 删除索引的消息监听器
 */
public class DeleteListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        //从消息中获取goodsid
        try {
            String goodsId = ((TextMessage)message).getText();
            //根据goodsid将索引库中的数据删除
            SimpleQuery query = new SimpleQuery("item_goodsid:" + Long.parseLong(goodsId));
            solrTemplate.delete(query);
            solrTemplate.commit();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
