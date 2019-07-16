package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

public interface BrandService {
    List<TbBrand> findAll();

    PageResult<TbBrand> findPage(int page, int size);

    TbBrand findOne(long id);

    void save(TbBrand brand);

    void delete(String ids);
}
