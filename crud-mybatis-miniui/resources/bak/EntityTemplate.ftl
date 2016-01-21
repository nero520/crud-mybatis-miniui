package ${bussPackage}.entity;

#foreach($po in $!{feilds})
#if	($po.columnName !='create_date'&&$po.columnName !='modify_date'&&$po.columnName !='createDate'&&$po.columnName !='modifyDate')
#if	($po.columnType =='datetime')
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
#break
#end  
#end  
#end
import com.msds.base.entity.BaseEntity;
import java.math.BigDecimal;
/**
 * 
 * <br>
 * <b>功能：</b>${className}Entity<br>
 * <b>作者：</b>lilong<br>
 * <b>日期：</b> ${dateTime} <br>
 * <b>版权所有：<b>版权所有(C) 2014，wwww.minshengec.com<br>
 * <b>此类为自动生成<br>
 */
public class ${className}Entity extends BaseEntity {
	
#foreach($po in $!{feilds})
#if	($po.columnName !='create_date'&&$po.columnName !='modify_date'&&$po.columnName !='createDate'&&$po.columnName !='modifyDate')
	/**${po.ColumnCommentLong}*/
	private ${po.dataType} ${po.columnName};
#end
#end

	#foreach($po in $!{feilds})
#if	($po.columnName !='create_date'&&$po.columnName !='modify_date'&&$po.columnName !='createDate'&&$po.columnName !='modifyDate')
	/**
	 *方法: 取得${po.dataType}
	 *@return: ${po.dataType}  ${po.ColumnCommentLong}
	 */
	public ${po.dataType} get${po.columnName.substring(0, 1).toUpperCase()}${po.columnName.substring(1)}(){
		return this.${po.columnName};
	}
#end
#if	($po.columnName !='create_date'&&$po.columnName !='modify_date'&&$po.columnName !='createDate'&&$po.columnName !='modifyDate'&&$po.columnName !='id')
	/**
	 *方法: 设置${po.dataType}
	 *@param: ${po.dataType}  ${po.ColumnCommentLong}
	 */
#if	($po.columnType =='datetime')
	@DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")  
	@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss",timezone = "GMT+8")
#end  
	public void set${po.columnName.substring(0, 1).toUpperCase()}${po.columnName.substring(1)}(${po.dataType} ${po.columnName}){
		this.${po.columnName} = ${po.columnName};
	}
#end
#end
}

