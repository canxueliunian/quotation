package com.ifast.quotation.dao;

import com.ifast.quotation.domain.ItemDO;
import com.ifast.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * <pre>
 * 条目表
 * </pre>
 * <small> 2019-01-03 13:47:22 | canxue</small>
 */
public interface ItemDao extends BaseDao<ItemDO> {
// 插入关联关系
    boolean insertLink(ItemDO entity);

    Long selectEntryId(@Param("itemId") Long itemId);

    ItemDO getWholeItemById(Long itemId);
}
