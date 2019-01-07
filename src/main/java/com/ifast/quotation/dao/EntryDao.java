package com.ifast.quotation.dao;

import com.ifast.quotation.domain.EntryDO;
import com.ifast.common.base.BaseDao;
import com.ifast.quotation.domain.ItemDO;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * <pre>
 * 条目信息
rateString : 为通过上面的rate 两个字段生成的额每周一次, 每日一次的对应信息
显示时,显示该字段, 在修改时同时维护该字段
 * </pre>
 * <small> 2019-01-02 11:15:12 | canxue</small>
 */
public interface EntryDao extends BaseDao<EntryDO> {
    // 插入关联关系
    boolean insertLink(EntryDO entity);

    Long selectItemIdByEntryId(@Param("entryId") Long entryId);
}
