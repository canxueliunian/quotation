package com.ifast.quotation.service.impl;

import com.ifast.quotation.dao.EntryDao;
import com.ifast.quotation.domain.EntryDO;
import org.springframework.stereotype.Service;

import com.ifast.quotation.dao.ItemDao;
import com.ifast.quotation.domain.ItemDO;
import com.ifast.quotation.service.ItemService;
import com.ifast.common.base.CoreServiceImpl;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <pre>
 * 条目表
 * </pre>
 * <small> 2019-01-03 13:47:22 | canxue</small>
 */
@Service
public class ItemServiceImpl extends CoreServiceImpl<ItemDao, ItemDO> implements ItemService {
    @Resource
    EntryDao entryDao;









    /**
     * todo 目前先使用代码的关联查询, 之后有时间的话,将其改为mapper查询
     *
     * @param itemId
     * @return
     */
    @Override
    public ItemDO getWholeItemById(Long itemId) {
        ItemDO itemDO = baseMapper.selectById(itemId);
        List<Long> entryIds = baseMapper.selectEntryId(itemId);
        List<EntryDO> entryDOS = entryDao.selectBatchIds(entryIds);
        itemDO.setEntryDOList(entryDOS);
        return itemDO;
//        return baseMapper.getWholeItemById(itemId);
    }
}
