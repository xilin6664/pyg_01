package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class ItemPageServiceImpl implements ItemPageService {
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
    private TbItemMapper itemMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Override
    public void geneItemHtml(Long goodsId) throws Exception {
        staticPageByGoodsId(goodsId);


    }
//根据goodsid生成静态页面
    private void staticPageByGoodsId(Long goodsId) throws Exception {
        //准备模板文件
        //创建configuration变量
        //设置参数:版本/模板目录/默认编码格式
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        //根据模板名称获取模板变量
        Template template = configuration.getTemplate(PAGE_TEMPLATE_NAME);
        //准备数据:查询goods/goodsDesc/item三张表数据
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        //根据商品的三级分类id查询三级分类名称放到freemarker数据模板中
        TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
        TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
        TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
        //查询item数据
        TbItemExample example = new TbItemExample();
        example.createCriteria().andStatusEqualTo("1").andGoodsIdEqualTo(goodsId);//商品对应的有效的库存对象
        List<TbItem> itemList = itemMapper.selectByExample(example);
        //构造模板数据对象
        Map map = new HashMap();
        map.put("goods", tbGoods);
        map.put("goodsDesc", goodsDesc);
        map.put("cat1Name", itemCat1.getName());
        map.put("cat2Name", itemCat2.getName());
        map.put("cat3Name", itemCat3.getName());
        map.put("itemList", itemList);
        for (TbItem item : itemList) {
            map.put("item", item);
            //合并数据和模板生成静态页面到指定位置:
            Writer out = new FileWriter(new File(PAGE_STATIC_DIR + item.getId() + ".html"));
            template.process(map,out);
            //关闭流
            out.close();
        }
        //将生成模板之后的商品状态修改成以生成模板的状态
    }

    @Override//全部生成静态模板
    public void goodsHtmlAll() throws Exception {
        //查询出所有的符合条件的商品数据
        TbGoodsExample example = new TbGoodsExample();
        //未删除状态且审核通过
        example.createCriteria().andIsDeleteEqualTo("0").andAuditStatusEqualTo("2");
        List<TbGoods> goodsList = goodsMapper.selectByExample(example);
        for (TbGoods tbGoods : goodsList) {
            staticPageByGoodsId(tbGoods.getId());//调用方法逐一生成静态页面
        }
    }
}
