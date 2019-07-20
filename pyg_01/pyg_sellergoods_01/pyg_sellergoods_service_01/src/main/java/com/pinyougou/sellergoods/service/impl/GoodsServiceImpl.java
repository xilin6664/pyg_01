package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.vo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.transaction.annotation.Transactional;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加 SKU库存信息
	 * @param goods
	 */
	@Override
	public void add(Goods goods) {
		//保存主表信息并返回保存的主键
		TbGoods tbGoods = goods.getTbGoods();
		goodsMapper.insertSelective(tbGoods);
		//设置从表信息的主键,并保存从表信息
		TbGoodsDesc goodsDesc = goods.getTbGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		tbGoodsDescMapper.insertSelective(goodsDesc);
		//3.保存库存列表数据
		//3.1判断是否启用规格
		if("1".equals(tbGoods.getIsEnableSpec())){
			//3.2启用，保存goods.itemList中的数据
			for(TbItem item : goods.getItemList()){
				//标题
				String title = goods.getTbGoods().getGoodsName();
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValus(goods, item);
				itemMapper.insert(item);
			}
		} else {
			//3.3未启用，创建tbItem对象，保存到数据库
			TbItem item = new TbItem();
			item.setTitle(goods.getTbGoods().getGoodsName());//商品 SPU+规格描述串作为SKU 名称
			item.setPrice(goods.getTbGoods().getPrice());//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存数量
			item.setSpec("{}");
			setItemValus(goods, item);
			itemMapper.insert(item);
		}
	}
	private void setItemValus(Goods goods,TbItem item) {
		item.setGoodsId(goods.getTbGoods().getId());//商品 SPU 编号
		item.setSellerId(goods.getTbGoods().getSellerId());//商家编号
		item.setCategoryid(goods.getTbGoods().getCategory3Id());//商品分类编号（3 级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期
		//品牌名称
		TbBrand brand =
				brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
		if (brand != null) {
			item.setBrand(brand.getName());
		}
		//分类名称
		TbItemCat itemCat =
				itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
		item.setCategory(itemCat.getName());
		//商家名称
		TbSeller seller =
				sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
		item.setSeller(seller.getNickName());
		//图片地址（取 spu 的第一个图片）
		List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),
				Map.class) ;
		if(imageList.size()>0){
			item.setImage ( (String)imageList.get(0).get("url"));
		}
	}

	
	/**
	 * 修改
     * @param goods
     */
	@Override
	public void update(Goods goods){
		//更新主表信息
		TbGoods tbGoods = goods.getTbGoods();
		goodsMapper.updateByPrimaryKeySelective(tbGoods);
		//更新从表信息
		TbGoodsDesc goodsDesc = goods.getTbGoodsDesc();
		tbGoodsDescMapper.updateByPrimaryKeySelective(goodsDesc);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableEqualTo(goods.getIsMarketable());
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteEqualTo(goods.getIsDelete());
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override//批量修改商品状态
    public void updateAuditStatus(String auditStatus, Long[] selectIds) {
        for (Long selectId : selectIds) {
            TbGoods tbGoods = new TbGoods();
            tbGoods.setId(selectId);
            tbGoods.setAuditStatus(auditStatus);
            goodsMapper.updateByPrimaryKeySelective(tbGoods);
        }
    }

	@Override//批量删除商品
	public void deleteGoods(Long[] selectIds) {
		for (Long selectId : selectIds) {
			TbGoods tbGoods = new TbGoods();
			tbGoods.setId(selectId);
			tbGoods.setIsDelete("1");//更新为已删除状态
			goodsMapper.updateByPrimaryKeySelective(tbGoods);
		}
	}

    @Override//商品上下架管理
    public void setMarketableStatus(String marketableStatus, Long[] selectIds) {
		for (Long selectId : selectIds) {
			TbGoods tbGoods = new TbGoods();
			tbGoods.setId(selectId);
			tbGoods.setIsMarketable(marketableStatus);//更新上下架状态
			goodsMapper.updateByPrimaryKeySelective(tbGoods);
		}
    }

}
