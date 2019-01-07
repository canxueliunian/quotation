package com.ifast.quotation.controller;


import java.util.Arrays;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ifast.common.annotation.Log;
import com.ifast.common.base.AdminBaseController;
import com.ifast.quotation.domain.HotelDO;
import com.ifast.quotation.service.HotelService;
import com.ifast.common.utils.Result;

/**
 * 
 * <pre>
 * 酒店表 

 * </pre>
 * <small> 2019-01-07 18:14:17 | canxue</small>
 */
@Controller
@RequestMapping("/quotation/hotel")
public class HotelController extends AdminBaseController {
	@Autowired
	private HotelService hotelService;
	
	@GetMapping()
	@RequiresPermissions("quotation:hotel:hotel")
	String Hotel(){
	    return "quotation/hotel/hotel";
	}
	
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("quotation:hotel:hotel")
	public Result<IPage<HotelDO>> list(HotelDO hotelDTO){
        QueryWrapper<HotelDO> wrapper = new QueryWrapper<HotelDO>().orderByDesc("id");
        IPage<HotelDO> page = hotelService.page(getPage(HotelDO.class), wrapper);
        return Result.ok(page);
	}
	
	@GetMapping("/add")
	@RequiresPermissions("quotation:hotel:add")
	String add(){
	    return "quotation/hotel/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("quotation:hotel:edit")
	String edit(@PathVariable("id") Long id,Model model){
		HotelDO hotel = hotelService.getById(id);
		model.addAttribute("hotel", hotel);
	    return "quotation/hotel/edit";
	}
	
	@Log("添加酒店表 ")
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("quotation:hotel:add")
	public Result<String> save( HotelDO hotel){
		hotelService.save(hotel);
        return Result.ok();
	}
	
	@Log("修改酒店表")
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("quotation:hotel:edit")
	public Result<String>  update( HotelDO hotel){
		boolean update = hotelService.updateById(hotel);
		return update ? Result.ok() : Result.fail();
	}
	
	@Log("删除酒店表")
	@PostMapping( "/remove")
	@ResponseBody
	@RequiresPermissions("quotation:hotel:remove")
	public Result<String>  remove( Long id){
		hotelService.removeById(id);
        return Result.ok();
	}
	
	@Log("批量删除酒店表 ")
	@PostMapping( "/batchRemove")
	@ResponseBody
	@RequiresPermissions("quotation:hotel:batchRemove")
	public Result<String>  remove(@RequestParam("ids[]") Long[] ids){
		hotelService.removeByIds(Arrays.asList(ids));
		return Result.ok();
	}
	
}
