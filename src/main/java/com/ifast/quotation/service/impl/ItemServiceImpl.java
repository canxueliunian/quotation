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
     * 同时进行新增关联关系以及条目内容
     *
     * @param entity
     * @return
     */
    @Override
    public boolean save(ItemDO entity) {
        // 新增关联关系
        boolean flag = baseMapper.insertLink(entity);
        return super.save(entity);
    }

    /**
     * 进行修改操作, 如果发生了时间或者金额的变化,则进行关联entry修改
     * 如果未发生的话则只修改自身
     *
     * @param entity
     * @return
     */
//todo 将该部分代码转移到entry中去
//    @Override
//    public boolean updateById(ItemDO entity) {
//        ItemDO oldItemDO = baseMapper.selectById(entity.getId());
//        Long entryId = baseMapper.selectEntryId(entity.getId());
//        EntryDO entryDO = entryDao.selectById(entryId);
//        boolean isSame = isSame(oldItemDO, entity, entryDO);
//        if (isSame) {
//            return super.updateById(entity);
//        } else {
//            entryDao.updateById(entryDO);
//            return super.updateById(entity);
//        }
//    }

    /**
     * 普通的删除操作
     * 删除操作,同时修改entry对应的值
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        ItemDO oldItemDO = baseMapper.selectById(id);
        Long entryId = baseMapper.selectEntryId(Long.valueOf((String) id));
        EntryDO entryDO = entryDao.selectById(entryId);
        ItemDO itemDO = new ItemDO();
        itemDO.setSpendpay(new BigDecimal(0));
        itemDO.setSpendtime(Double.NaN);
        boolean isSame = isSame(oldItemDO, itemDO, entryDO);
        entryDao.updateById(entryDO);
        return super.removeById(id);
    }

    /**
     * 判断新旧对象是否发生时间以及金额上的修改, 为发生返回true
     * 发生返回fasle
     * 并重新设置entry对应的值
     * <p>
     * 如果是多选包的话, 要同时维护上面层级的内容
     *
     * @param oldItemDO
     * @param newItemDo
     * @param entryDO
     * @return
     */
    private boolean isSame(ItemDO oldItemDO, ItemDO newItemDo, EntryDO entryDO) {
        boolean flag = true;
        BigDecimal spendpay = newItemDo.getSpendpay();
        Double spendtime = newItemDo.getSpendtime();
        BigDecimal oldSpendpay = oldItemDO.getSpendpay();
        Double oldSpendtime = oldItemDO.getSpendtime();
        if (Double.compare(oldSpendtime, spendtime) != 0) {
            double changeTime = spendtime - oldSpendtime;
            oldItemDO.setSpendtime(changeTime);
            Double entryTime = entryDO.getSpendtime();
            entryTime = entryTime + changeTime;
            entryDO.setSpendtime(entryTime);
            flag = false;
        }
        if (spendpay.compareTo(oldSpendpay) != 0) {
            BigDecimal changePay = spendpay.subtract(oldSpendpay);
            oldItemDO.setSpendpay(changePay);
            BigDecimal entryPay = entryDO.getSpendpay();
            entryPay = entryPay.add(changePay);
            entryDO.setSpendpay(entryPay);
            flag = false;
        }
        return flag;
    }


    @Override
    public ItemDO getWholeItemById(Long itemId) {
        return baseMapper.getWholeItemById(itemId);
    }
}
