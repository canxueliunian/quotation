package com.ifast.quotation.service.impl;

import com.ifast.quotation.dao.ItemDao;
import com.ifast.quotation.domain.ItemDO;
import org.springframework.stereotype.Service;

import com.ifast.quotation.dao.EntryDao;
import com.ifast.quotation.domain.EntryDO;
import com.ifast.quotation.service.EntryService;
import com.ifast.common.base.CoreServiceImpl;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <pre>
 * 条目信息
 * rateString : 为通过上面的rate 两个字段生成的额每周一次, 每日一次的对应信息
 * 显示时,显示该字段, 在修改时同时维护该字段
 * </pre>
 * <small> 2019-01-02 11:15:12 | canxue</small>
 */
@Service
public class EntryServiceImpl extends CoreServiceImpl<EntryDao, EntryDO> implements EntryService {

    @Resource
    ItemDao itemDao;

    /**
     * 同时进行新增关联关系以及条目内容
     * 对于多选包的父选项, 将其时间以及花费设置为0
     *
     * @param entity
     * @return
     */
    @Override
    public boolean save(EntryDO entity) {
        // 新增关联关系
        boolean flag = baseMapper.insertLink(entity);
        Integer packagetype = entity.getPackagetype();
        Integer entrytype = entity.getEntrytype();
        if (packagetype == 3 && entrytype == 1) {
            entity.setSpendpay(BigDecimal.ZERO);
            entity.setSpendtime(0.0);
        }
        // 查询出关联的itemDo
        ItemDO itemDO = itemDao.selectById(entity.getItemId());
        BigDecimal spendpay = entity.getSpendpay();
        Double spendtime = entity.getSpendtime();
        // 如果是多选包的第二级, 则要同时修改关联的上级entryDo
        if (entity.getParentid() != null) {
            EntryDO parentDo = getById(entity.getParentid());
            parentDo.setSpendtime(parentDo.getSpendtime() + spendtime);
            parentDo.setSpendpay(parentDo.getSpendpay().add(spendpay));
            updateById(parentDo);
        }
        // 修改关联的itemDo
        itemDO.setSpendtime(itemDO.getSpendtime() + spendtime);
        itemDO.setSpendpay(itemDO.getSpendpay().add(spendpay));
        itemDao.updateById(itemDO);
        return super.save(entity);
    }

    /**
     * 进行修改操作, 如果发生了时间或者金额的变化,则进行关联entry修改
     * 如果未发生的话则只修改自身
     *
     * @param newEntryDO
     * @return
     */
    @Override
    public boolean updateById(EntryDO newEntryDO) {
        EntryDO oldEntryDo = baseMapper.selectById(newEntryDO.getId());
        Long itemId = baseMapper.selectItemIdByEntryId(newEntryDO.getId()); // 这个地方itemId 应该可以直接传过来
        ItemDO itemDO = itemDao.selectById(itemId);
        boolean parentFlag = false;
        Long parentid = oldEntryDo.getParentid();
        EntryDO parentDo = new EntryDO();
        if (parentid != null) {
            parentFlag = true;
            parentDo = getById(parentid);

        }

        boolean isSame = isSame(oldEntryDo, newEntryDO, parentDo, itemDO);
        if (isSame) {
            return super.updateById(newEntryDO);
        } else {
            itemDao.updateById(itemDO);
            if (parentFlag) {
                updateById(parentDo);
            }
            return super.updateById(newEntryDO);
        }
    }

    /**
     * 判断新旧对象是否发生时间以及金额上的修改, 为发生返回true
     * 发生返回fasle
     * 并重新设置entry对应的值
     * <p>
     * 如果是多选包的话, 要同时维护上面层级的内容
     *
     * @param oldEntryDo
     * @param newEntryDo
     * @param itemDO
     * @return
     */
    private boolean isSame(EntryDO oldEntryDo, EntryDO newEntryDo, EntryDO parentDo, ItemDO itemDO) {
        boolean flag = true;
        BigDecimal spendpay = newEntryDo.getSpendpay();
        Double spendtime = newEntryDo.getSpendtime();
        BigDecimal oldSpendpay = oldEntryDo.getSpendpay();
        Double oldSpendtime = oldEntryDo.getSpendtime();


        if (Double.compare(oldSpendtime, spendtime) != 0) {
            double changeTime = spendtime - oldSpendtime;
            // 设置entry时间
            Double entryTime = itemDO.getSpendtime() + changeTime;
            itemDO.setSpendtime(entryTime);
            // 设置 parentDo的时间
            if (parentDo != null) {
                Double ptime = parentDo.getSpendtime() + changeTime;
                parentDo.setSpendtime(ptime);
            }

            flag = false;
        }
        if (spendpay.compareTo(oldSpendpay) != 0) {
            BigDecimal changePay = spendpay.subtract(oldSpendpay);
            BigDecimal entryPay = itemDO.getSpendpay();
            entryPay = entryPay.add(changePay);
            itemDO.setSpendpay(entryPay);
            if (parentDo != null) {
                BigDecimal add = parentDo.getSpendpay().add(changePay);
                parentDo.setSpendpay(add);
            }
            flag = false;
        }
        return flag;
    }

    /**
     * 普通的删除操作
     * 删除操作,同时修改entry对应的值
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        EntryDO oldEnteyDo = getById(id);
        // 查询出关联的itemId
        Long itemId = baseMapper.selectItemIdByEntryId(Long.valueOf(id.toString()));
        ItemDO itemDO = itemDao.selectById(itemId);
        Long parentid = oldEnteyDo.getParentid();
        EntryDO parentDo = new EntryDO();
        boolean parentFlag = false;
        if (parentid != null) {
            parentFlag = true;
            parentDo = getById(parentid);
        }
        Long entryId = null;
        EntryDO newEntryDo = new EntryDO();
        newEntryDo.setSpendpay(new BigDecimal(0));
        newEntryDo.setSpendtime(Double.NaN);
        boolean isSame = isSame(oldEnteyDo, newEntryDo, parentDo, itemDO);
        // 修改关联的itemDO
        itemDao.updateById(itemDO);
        // 修改关联的parentDo
        if (parentFlag) {
            updateById(parentDo);
        }
        return super.removeById(id);
    }


}
