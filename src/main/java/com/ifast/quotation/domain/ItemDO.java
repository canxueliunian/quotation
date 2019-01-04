package com.ifast.quotation.domain;

import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.ifast.common.base.BaseDO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ifast.common.annotation.*;


/**
 * <pre>
 * 条目表
 * </pre>
 * <small> 2019-01-03 13:47:22 | canxue</small>
 */
@Data
@SuppressWarnings("serial")
@TableName("db_item")
@EqualsAndHashCode(callSuper = true)
public class ItemDO extends Model<ItemDO> {
    @TableId
    @Excel(name = "id")
    private Long id;

    /**
     * 项目名称
     */
    @Excel(name = "项目名称")
    private String itemname;

    /**
     * 项目类型
     */
    @Excel(name = "项目类型")
    private Integer packagetype;

    /**
     * 花费时间
     */
    @Excel(name = "花费时间")
    private Double spendtime;

    /**
     * 花费金额
     */
    @Excel(name = "花费金额")
    private BigDecimal spendpay;

    /**
     * 状态
     */
    @Excel(name = "状态")
    @TableLogic
    private Integer status;

    /**  */
    @Excel(name = "创建时间")
    private Date gmtcreate;

    /**  */
    @Excel(name = "修改时间")
    private Date gmtmodify;
    /**
     * 开关 0 表示正常使用 1 表示关闭
     */
    @Excel(name = "开关")
    private Integer onoff;
    /**
     * 其关联的entrydo, 用来进行构造完整对象时进行使用
     */
    @TableField(exist = false)
    private List<EntryDO> entryDOList;

//    @TableField(exist = false)
//    private Long entryId;

}
