package com.pinyougou.page.listener;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.util.List;

/**
 * 将下架的商品的静态页面删除
 */
public class DeleteListener implements MessageListener {
    @Autowired
    private TbItemMapper itemMapper;
    @Value("${PAGE_STATIC_DIR}")
    private String PAGE_STATIC_DIR;
    @Override
    public void onMessage(Message message) {
        //从消息中获取goodsId
        try {
            String goodsId = ((TextMessage)message).getText();
            //根据goodsId查询库存列表信息
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(Long.valueOf(goodsId)).andStatusEqualTo("1");
            List<TbItem> itemList = itemMapper.selectByExample(example);
            for (TbItem tbItem : itemList) {
                //根据对象的id动态拼接文件路径:  D:\文件夹\[id].html
                //使用文件的删除方法删除文件
                new File(PAGE_STATIC_DIR + tbItem.getId() + ".html").delete();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }


    }
}
