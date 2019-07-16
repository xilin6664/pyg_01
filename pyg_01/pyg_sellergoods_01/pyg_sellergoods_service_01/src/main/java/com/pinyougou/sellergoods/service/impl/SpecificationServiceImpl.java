package com.pinyougou.sellergoods.service.impl;
import java.util.List;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.springframework.transaction.annotation.Transactional;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 * @param specification
	 */
	@Override
	public void add(Specification specification) {
		//判断id是否为null
		if (null == specification.getSpec().getId()) {
			//保存规格信息
			TbSpecification spec = specification.getSpec();
			specificationMapper.insert(spec);//保存规格主表信息,在保存之后返回主键的值
			List<TbSpecificationOption> optionList = specification.getOptionList();
			for (TbSpecificationOption option : optionList) {
				option.setSpecId(spec.getId());//设置从表外键值
				tbSpecificationOptionMapper.insert(option);//保存从表信息
			}
		} else {
			//修改规格信息.先将之前对应的从表信息删除,再将修改后的内容保存到从表中
			TbSpecification spec = specification.getSpec();
			specificationMapper.updateByPrimaryKeySelective(spec);
			//根据主表id删除从表信息
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(spec.getId());
			tbSpecificationOptionMapper.deleteByExample(example);
			//保存从表修改之后的信息
			List<TbSpecificationOption> optionList = specification.getOptionList();
			for (TbSpecificationOption option : optionList) {
				option.setSpecId(spec.getId());//设置从表外键值
				tbSpecificationOptionMapper.insert(option);//保存从表信息
			}
		}
	}

	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){

		TbSpecification spec = specificationMapper.selectByPrimaryKey(id);
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.createCriteria().andSpecIdEqualTo(id);
		List<TbSpecificationOption> optionList = tbSpecificationOptionMapper.selectByExample(example);
		return new Specification(spec, optionList);
	}

	/**
	 * 批量删除
	 * @param ids
	 */
	@Override
	public void delete(String ids) {
		String[] idArr = ids.split(",");
		for (String s : idArr) {
			//删除主表信息
			Long id = Long.valueOf(s);
			specificationMapper.deleteByPrimaryKey(id);
			//删除从表信息
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.createCriteria().andSpecIdEqualTo(id);
			tbSpecificationOptionMapper.deleteByExample(example);

		}

	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
