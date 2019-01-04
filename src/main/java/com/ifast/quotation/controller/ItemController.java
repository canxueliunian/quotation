package com.ifast.quotation.controller;


import java.util.Arrays;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.ifast.common.annotation.Log;
import com.ifast.common.base.AdminBaseController;
import com.ifast.quotation.domain.ItemDO;
import com.ifast.quotation.service.ItemService;
import com.ifast.common.utils.Result;

import javax.annotation.Resource;

/**
 * <pre>
 * 条目表
 * </pre>
 * <small> 2019-01-03 13:47:22 | canxue</small>
 */
@Controller
@RequestMapping("/quotation/item")
public class ItemController extends AdminBaseController {
    @Autowired
    private ItemService itemService;

    @GetMapping()
    @RequiresPermissions("quotation:item:item")
    String Item() {
        return "quotation/item/item";
    }

    @ResponseBody
    @GetMapping("/list")
    @RequiresPermissions("quotation:item:item")
    public Result<IPage<ItemDO>> list(ItemDO itemDTO) {
        QueryWrapper<ItemDO> wrapper = new QueryWrapper<ItemDO>().orderByDesc("id");
        IPage<ItemDO> page = itemService.page(getPage(ItemDO.class), wrapper);
        return Result.ok(page);
    }

    @GetMapping("/add")
    @RequiresPermissions("quotation:item:add")
    String add() {
        return "quotation/item/add";
    }

    @GetMapping("/edit/{id}")
    @RequiresPermissions("quotation:item:edit")
    String edit(@PathVariable("id") Long id, Model model) {
        ItemDO item = itemService.getById(id);
        model.addAttribute("item", item);
        return "quotation/item/edit";
    }

    @Log("添加条目表")
    @ResponseBody
    @PostMapping("/save")
    @RequiresPermissions("quotation:item:add")
    public Result<String> save(ItemDO item) {
        try {
            itemService.save(item);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @Log("修改条目表")
    @ResponseBody
    @RequestMapping("/update")
    @RequiresPermissions("quotation:item:edit")
    public Result<String> update(ItemDO item) {

        ItemDO itemDO = new ItemDO();
        itemDO.setId(item.getId());
        itemDO.setItemname(item.getItemname());
        boolean update = itemService.updateById(itemDO);
        return update ? Result.ok() : Result.fail();
    }

    @Log("查看条目细则")
    @ResponseBody
    @RequestMapping("/viewdetail")
    @RequiresPermissions("quotation:item:edit")
    public Result<ItemDO> viewdetail(Long itemId) {
        ItemDO itemDO =   itemService.getWholeItemById(itemId);


        return true ? Result.ok() : Result.fail();
    }


    @Log("启动停用条目")
    @PostMapping(value = "/changeonOff")
    @ResponseBody
    public Result<String> changeJobStatus(ItemDO itemDO) {
        String label = "停止";
        Integer onoff = itemDO.getOnoff();
        if (onoff == 0) {
            label = "启用";
        } else {
            label = "停用";
        }
        try {
            itemService.updateById(itemDO);
            return Result.ok("任务" + label + "成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok("任务" + label + "失败");
    }

}
/**
 * 暂时不用的内容
 */
/*


    @Log("删除条目表")
    @PostMapping("/remove")
    @ResponseBody
    @RequiresPermissions("quotation:item:remove")
    public Result<String> remove(Long id) {
        itemService.removeById(id);
        return Result.ok();
    }
      @Log("批量删除条目表")
    @PostMapping("/batchRemove")
    @ResponseBody
    @RequiresPermissions("quotation:item:batchRemove")
    public Result<String> remove(@RequestParam("ids[]") Long[] ids) {
        itemService.removeByIds(Arrays.asList(ids));
        return Result.ok();
    }


 */