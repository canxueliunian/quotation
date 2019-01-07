package com.ifast.quotation.controller;


import java.util.Arrays;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifast.common.annotation.Log;
import com.ifast.common.base.AdminBaseController;
import com.ifast.quotation.domain.EntryDO;
import com.ifast.quotation.service.EntryService;
import com.ifast.common.utils.Result;

/**
 * 
 * <pre>
 * 条目信息
 * </pre>
 * <small> 2019-01-02 11:15:12 | canxue</small>
 */
@Controller
@RequestMapping("/quotation/entry")
public class EntryController extends AdminBaseController {
	@Autowired
	private EntryService entryService;
	
	@GetMapping()
	@RequiresPermissions("quotation:entry:entry")
	String Entry(){
	    return "quotation/entry/entry";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("quotation:entry:entry")
	public Result<IPage<EntryDO>> list(EntryDO entryDTO){
        Wrapper<EntryDO> wrapper = new QueryWrapper<EntryDO>().orderByDesc("id");
        IPage<EntryDO> page = entryService.page(getPage(EntryDO.class), wrapper);
        return Result.ok(page);
	}


	@GetMapping("/add")
	@RequiresPermissions("quotation:entry:add")
	String add(){
	    return "quotation/entry/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("quotation:entry:edit")
	String edit(@PathVariable("id") Long id,Model model){
		EntryDO entry = entryService.getById(id);
		model.addAttribute("entry", entry);
	    return "quotation/entry/edit";
	}
	
	@Log("添加条目信息")
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("quotation:entry:add")
	public Result<String> save( EntryDO entry){
		entryService.save(entry);
        return Result.ok();
	}
	
	@Log("修改条目信息 ")
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("quotation:entry:edit")
	public Result<String>  update( EntryDO entry){
		boolean update = entryService.updateById(entry);
		return update ? Result.ok() : Result.fail();
	}
	

	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("quotation:entry:remove")
	public Result<String>  remove( Long id){
		entryService.removeById(id);
        return Result.ok();
	}
	

	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("quotation:entry:batchRemove")
	public Result<String>  remove(@RequestParam("ids[]") Long[] ids){
		entryService.removeByIds(Arrays.asList(ids));
		return Result.ok();
	}
	
}
