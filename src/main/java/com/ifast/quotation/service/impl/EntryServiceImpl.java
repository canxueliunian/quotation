package com.ifast.quotation.service.impl;

import org.springframework.stereotype.Service;

import com.ifast.quotation.dao.EntryDao;
import com.ifast.quotation.domain.EntryDO;
import com.ifast.quotation.service.EntryService;
import com.ifast.common.base.CoreServiceImpl;

/**
 * 
 * <pre>
 * 条目信息
rateString : 为通过上面的rate 两个字段生成的额每周一次, 每日一次的对应信息
显示时,显示该字段, 在修改时同时维护该字段
 * </pre>
 * <small> 2019-01-02 11:15:12 | canxue</small>
 */
@Service
public class EntryServiceImpl extends CoreServiceImpl<EntryDao, EntryDO> implements EntryService {

}
