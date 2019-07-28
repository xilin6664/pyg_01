package com.pinyougou.page.listener;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.impl.ItemPageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 将上架的商品生成静态页
 */
public class UpdateListener implements MessageListener {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${PAGE_TEMPLATE_NAME}")
    private String PAGE_TEMPLATE_NAME;
    @Value("${PAGE_STATIC_DIR}")
    private String PAGE_STATIC_DIR;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public void onMessage(Message message) {
    //从消息中获取goods
        try {
            String goodsId =((TextMessage)message).getText();
            ItemPageServiceImpl pageService = new ItemPageServiceImpl();
            pageService.geneItemHtml(Long.valueOf(goodsId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //根据goodsid生成静态页面
    }
}
