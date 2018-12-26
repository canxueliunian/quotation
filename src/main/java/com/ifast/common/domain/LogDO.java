package com.ifast.common.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * </pre>
 * <small> 2018年3月23日 | Aron</small>
 */
@TableName("sys_log")
@Data
public class LogDO extends Model<LogDO> implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = -938654836571738415L;
    
    @TableId
    private Long id;

    private Long userId;

    private String username;

    private String operation;

    private Integer time;

    private String method;

    private String params;

    private String ip;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}