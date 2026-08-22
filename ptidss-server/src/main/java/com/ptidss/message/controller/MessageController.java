package com.ptidss.message.controller;

import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.message.domain.MessageRecord;
import com.ptidss.message.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 我的消息（对齐 OpenAPI V1.1 /message/**；分类/未读筛选/标记已读）
 */
@RestController
@RequestMapping("/message")
@RequiresPermissions("menu:message")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /** 我的消息（分类/未读，分页） */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String msgType,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "1") long pageNo,
            @RequestParam(defaultValue = "10") long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<MessageRecord> p =
                messageService.list(msgType, unreadOnly, pageNo, pageSize).getData();
        Map<String, Object> page = new LinkedHashMap<>();
        page.put("list", p.getRecords());
        page.put("pageNo", p.getCurrent());
        page.put("pageSize", p.getSize());
        page.put("total", p.getTotal());
        return Result.success(page);
    }

    /** 标记已读 */
    @PostMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        messageService.markRead(id);
        return Result.success();
    }
}
