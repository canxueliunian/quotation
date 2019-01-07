package com.ifast.quotation.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ifast.common.base.BaseDO;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ifast.common.annotation.*;


/**
 * 
 * <pre>
 * 酒店表 

 * </pre>
 * <small> 2019-01-07 18:14:17 | canxue</small>
 */
@Data
@SuppressWarnings("serial")
@TableName("db_hotel")
@EqualsAndHashCode(callSuper=true) 
public class HotelDO extends Model<HotelDO> {
	@TableId
	@Excel(name = "id")
	private Long id;

    /** 酒店名称 */
	@Excel(name = "$column.comment")
    private String hotelname;

    /** 酒店类型 */
	@Excel(name = "$column.comment")
    private Integer hoteltype;

    /** 店主姓名 */
	@Excel(name = "$column.comment")
    private String managename;

    /** 店长手机号码 */
	@Excel(name = "$column.comment")
    private String managerphone;

    /** 开业时间 */
	@Excel(name = "$column.comment")
    private Date openingdate;

    /** 省id */
	@Excel(name = "$column.comment")
    private String provinceid;

    /** 省name */
	@Excel(name = "$column.comment")
    private String provincename;

    /** 城市id */
	@Excel(name = "$column.comment")
    private String cityid;

    /** 城市name */
	@Excel(name = "$column.comment")
    private String cityname;

    /** 地区id */
	@Excel(name = "$column.comment")
    private String districtid;

    /** 地区name */
	@Excel(name = "$column.comment")
    private String districtname;

    /**  */
	@Excel(name = "$column.comment")
    private String detailaddress;

    /** 登记时间 */
	@Excel(name = "$column.comment")
    private Date registtime;

    /** 市场人员id */
	@Excel(name = "$column.comment")
    private Long marketmanid;

    /** 市场人员名称 */
	@Excel(name = "$column.comment")
    private String marketmanname;

    /**  */
	@Excel(name = "$column.comment")
    private Integer status;

    /**  */
	@Excel(name = "$column.comment")
    private Date gmtcreate;

    /**  */
	@Excel(name = "$column.comment")
    private Date gmtmodify;

    /** 图片路径 */
	@Excel(name = "$column.comment")
    private String imgpath;

}
