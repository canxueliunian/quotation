package com.ifast.quotation.service;

import com.ifast.quotation.domain.ItemDO;
import com.ifast.common.base.CoreService;

/**
 * 
 * <pre>
 * 条目表
 * </pre>
 * <small> 2019-01-03 13:47:22 | canxue</small>
 */
public interface ItemService extends CoreService<ItemDO> {

    ItemDO getWholeItemById(Long itemId);
}
