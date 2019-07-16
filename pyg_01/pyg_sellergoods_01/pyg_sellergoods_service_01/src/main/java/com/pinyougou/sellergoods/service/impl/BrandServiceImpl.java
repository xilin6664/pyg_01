package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override//分页展示
    public PageResult<TbBrand> findPage(int page, int size) {
        //设置分页参数
        PageHelper.startPage(page, size);
        //查询全部,并进行类型转换
        Page<TbBrand> brandPage = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        //构造返回值
        return new PageResult<>(brandPage.getTotal(),brandPage.getResult());
    }

    @Override//根据id查询品牌
    public TbBrand findOne(long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override//保存或修改品牌信息
    public void save(TbBrand brand) {
        if (null == brand.getId()) {
            //保存
            tbBrandMapper.insert(brand);
        }else {
            //更新
            tbBrandMapper.updateByPrimaryKey(brand);
        }
    }

    @Override//根据选中的id删除品牌
    public void delete(String ids) {
        String[] idArr = ids.split(",");
        for (String s : idArr) {

            tbBrandMapper.deleteByPrimaryKey(Long.valueOf(s));
        }
    }

}
