package ${bussPackage}.service.Impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msds.base.service.impl.BaseServiceImpl;
import ${bussPackage}.mapper.${className}Mapper;
import ${bussPackage}.entity.${className}Entity;
import ${bussPackage}.service.${className}Service;

/**
 * 
 * <br>
 * <b>功能：</b>${className}Service<br>
 * <b>作者：</b>lilong<br>
 * <b>日期：</b> ${dateTime} <br>
 * <b>版权所有：<b>版权所有(C) 2014，wwww.minshengec.com<br>
 * <b>此类为自动生成<br>
 */
@Service("$!{lowerName}Service")
public class ${className}ServiceImpl extends BaseServiceImpl<${className}Entity> implements ${className}Service{
	private final static Logger log= Logger.getLogger(${className}Service.class);

	@Autowired
    private ${className}Mapper ${lowerName}Mapper;
		
	public ${className}Mapper baseMapper() {
		return ${lowerName}Mapper;
	}

}
