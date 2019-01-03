package com.ifast.quotation.domain;

import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableId;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ifast.common.base.BaseDO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ifast.common.annotation.*;


/**
 * 
 * <pre>
 * 条目表
 * </pre>
 * <small> 2019-01-03 13:47:22 | canxue</small>
 */
@Data
@SuppressWarnings("serial")
@TableName("db_item")
@EqualsAndHashCode(callSuper=true) 
public class ItemDO extends BaseDO {
	@TableId
	@Excel(name = "id")
	private Long id;

    /** 项目名称 */
	@Excel(name = "${field.comment}")
    private String itemname;

    /** 项目类型 */
	@Excel(name = "${field.comment}")
    private Integer packagetype;

    /** 花费时间 */
	@Excel(name = "${field.comment}")
    private Double spendtime;

    /** 花费金额 */
	@Excel(name = "${field.comment}")
    private BigDecimal spendpay;

    /** 状态 */
	@Excel(name = "${field.comment}")
    private Integer status;

    /**  */
	@Excel(name = "${field.comment}")
    private Date gmtcreate;

    /**  */
	@Excel(name = "${field.comment}")
    private Date gmtmodify;

}
