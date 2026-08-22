package com.ptidss.market.service;

import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.market.dto.PricePoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 市场行情（对齐 OpenAPI V1.0 /market/**）
 * 数据源策略：`ptidss.market.data-source=mock` 时使用确定性模拟数据（种子=日期，可复现）；
 * TDengine 部署后切换为时序库查询实现（st_spot_price/st_supply_demand 超级表），接口契约不变
 */
@Service
public class MarketDataService {

    /** 数据源：mock（当前）/ tdengine */
    @Value("${ptidss.market.data-source:mock}")
    private String dataSource;

    /** 96 点时段：00:00-24:00 每 15 分钟 */
    private static final int POINTS = 96;

    public List<PricePoint> spotPrice(String marketType, String stage, Date startAt, Date endAt) {
        checkDataSource();
        if (StrUtils.isBlank(marketType) || StrUtils.isBlank(stage)) {
            throw new ServiceException("marketType/stage 不能为空");
        }
        List<PricePoint> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(startAt == null ? new Date() : startAt);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long seed = c.getTimeInMillis();
        double base = 380 + ((seed / 3600000) % 200);
        for (int i = 0; i < POINTS; i++) {
            PricePoint p = new PricePoint();
            p.setTs(c.getTime());
            p.setPrice(priceAt(base, i, seed, marketType, stage));
            p.setVolume(volumeAt(i, seed));
            p.setStage(stage);
            list.add(p);
            c.add(Calendar.MINUTE, 15);
            if (endAt != null && c.getTime().after(endAt)) {
                break;
            }
        }
        return list;
    }

    public List<PricePoint> midlongPrice(String variety, Date startAt, Date endAt) {
        checkDataSource();
        List<PricePoint> list = new ArrayList<>();
        // 中长期成交价格：按品种生成周/月粒度序列
        Calendar c = Calendar.getInstance();
        c.setTime(startAt == null ? new Date() : startAt);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long seed = c.getTimeInMillis();
        double base = 420 + ((seed / 86400000) % 120);
        int step = "annual".equals(variety) ? 31 : "monthly".equals(variety) ? 7 : 3;
        int max = "annual".equals(variety) ? 12 : 24;
        for (int i = 0; i < max; i++) {
            PricePoint p = new PricePoint();
            p.setTs(c.getTime());
            p.setPrice(BigDecimal.valueOf(base + (i * 7) % 30)
                    .setScale(2, RoundingMode.HALF_UP));
            p.setVolume(BigDecimal.valueOf(5000 + (seed + i * 137) % 9000)
                    .setScale(0, RoundingMode.HALF_UP));
            p.setStage("midlong");
            list.add(p);
            c.add(Calendar.DAY_OF_MONTH, step);
            if (endAt != null && c.getTime().after(endAt)) {
                break;
            }
        }
        return list;
    }

    public List<Map<String, Object>> supplyDemand(Date startAt, Date endAt) {
        checkDataSource();
        List<Map<String, Object>> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.setTime(startAt == null ? new Date() : startAt);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long seed = c.getTimeInMillis();
        for (int i = 0; i < POINTS; i++) {
            double hourFactor = 0.85 + 0.3 * Math.sin((i / 4) * Math.PI / 12.0 + 1.5); // 日负荷双峰
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("ts", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime()));
            m.put("loadValue", round2(32000 + (seed % 3000) * hourFactor));
            m.put("availableCapacity", round2(39000 + (seed % 1500)));
            m.put("renewableOutput", round2(6000 + (seed % 800) + 3500 * Math.abs(Math.sin(i / 12.0))));
            list.add(m);
            c.add(Calendar.MINUTE, 15);
            if (endAt != null && c.getTime().after(endAt)) {
                break;
            }
        }
        return list;
    }

    public Map<String, Object> heatmap(Date startDate, Date endDate) {
        checkDataSource();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        long seed = start.getTimeInMillis();
        List<String> dates = new ArrayList<>();
        List<Integer> points = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = (Calendar) start.clone();
        int dayCount = 0;
        while ((endDate == null || !c.getTime().after(endDate)) && dayCount < 31) {
            dates.add(fmt.format(c.getTime()));
            double base = 380 + ((seed / 3600000 + dayCount * 7) % 200);
            for (int i = 0; i < POINTS; i++) {
                points.add((int) Math.round(priceAt(base, i, seed + dayCount * 86400000L, "intra_province", "day_ahead")
                        .doubleValue()));
            }
            c.add(Calendar.DAY_OF_MONTH, 1);
            dayCount++;
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("dates", dates);
        resp.put("points", points);
        return resp;
    }

    /** 96 点电价曲线：双峰 + 新能源反调峰 + 噪声（确定性） */
    private BigDecimal priceAt(double base, int i, long seed, String marketType, String stage) {
        double hour = i / 4.0;
        double pattern = 0.9 + 0.35 * Math.sin((hour - 4) * Math.PI / 12.0)
                + 0.2 * Math.sin((hour - 12) * Math.PI / 12.0);   // 早峰 8 点 + 晚峰 19 点
        double noise = Math.sin(i * 13 + seed % 97) * 8;
        double interBonus = "inter_province".equals(marketType) ? 25 : 0;
        double realTimeBonus = "real_time".equals(stage) ? 15 : 0;
        return BigDecimal.valueOf(base * pattern + noise + interBonus + realTimeBonus)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal volumeAt(int i, long seed) {
        double hour = i / 4.0;
        double pattern = 0.8 + 0.25 * Math.sin((hour - 4) * Math.PI / 12.0);
        return BigDecimal.valueOf((30000 + (seed % 2000)) * pattern)
                .setScale(0, RoundingMode.HALF_UP);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private void checkDataSource() {
        if (!"mock".equals(dataSource) && !"tdengine".equals(dataSource)) {
            throw new ServiceException("市场数据源配置非法：" + dataSource);
        }
    }
}
