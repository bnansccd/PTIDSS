package com.ptidss.market.controller;

import com.ptidss.common.annotation.RequiresPermissions;
import com.ptidss.common.domain.Result;
import com.ptidss.market.dto.PricePoint;
import com.ptidss.market.service.MarketDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 市场行情（对齐 OpenAPI V1.0 /market/**：现货/中长期价格/供需/量价热力图）
 */
@RestController
@RequestMapping("/market")
@RequiresPermissions("menu:market")
public class MarketController {

    private final MarketDataService marketDataService;

    public MarketController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    /** 现货价格时序（96 点/区间） */
    @GetMapping("/price/spot")
    public Result<List<PricePoint>> spot(
            @RequestParam String marketType,
            @RequestParam String stage,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startAt,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endAt) {
        return Result.success(marketDataService.spotPrice(marketType, stage, startAt, endAt));
    }

    /** 中长期成交价格 */
    @GetMapping("/price/midlong")
    public Result<List<PricePoint>> midlong(
            @RequestParam(required = false) String variety,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startAt,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endAt) {
        return Result.success(marketDataService.midlongPrice(variety, startAt, endAt));
    }

    /** 供需形势（负荷/可用能力/新能源） */
    @GetMapping("/supply-demand")
    public Result<List<Map<String, Object>>> supplyDemand(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startAt,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endAt) {
        return Result.success(marketDataService.supplyDemand(startAt, endAt));
    }

    /** 量价热力图（日期×时段） */
    @GetMapping("/heatmap")
    public Result<Map<String, Object>> heatmap(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return Result.success(marketDataService.heatmap(startDate, endDate));
    }
}
