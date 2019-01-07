package com.ifast.quotation.dao;

import com.ifast.quotation.domain.ItemDO;
import com.ifast.common.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * <pre>
 * 条目表
 * </pre>
 * <small> 2019-01-03 13:47:22 | canxue</small>
 */
public interface ItemDao extends BaseDao<ItemDO> {


    List<Long> selectEntryId(@Param("itemId") Long itemId);

//    ItemDO getWholeItemById(Long itemId);
}
