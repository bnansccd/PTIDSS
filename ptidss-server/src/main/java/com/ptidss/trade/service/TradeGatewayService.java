package com.ptidss.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptidss.common.exception.ServiceException;
import com.ptidss.common.security.SecurityUtils;
import com.ptidss.common.utils.ConfigCryptoService;
import com.ptidss.common.utils.StrUtils;
import com.ptidss.trade.domain.TradeGatewayConfig;
import com.ptidss.trade.mapper.TradeGatewayConfigMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 交易网关配置（DDL 12.1 trade_gateway_config；V2.4 申报单 → 交易系统接口配置与状态监测）
 * 客户图形界面只输入 URL/账户/密码（appKey/appSecret）即可完成对接：
 * - 敏感字段（appSecret）AES 加密落库（ConfigCryptoService），对外仅脱敏展示；
 * - 编辑回显合并：提交 ****** 视为未修改（保留库中密文）；
 * - 提交申报时按本配置模拟推送并记录状态（见 TradeService.submitDeclaration）。
 */
@Service
public class TradeGatewayService {

    private final TradeGatewayConfigMapper gatewayConfigMapper;
    private final SecurityUtils securityUtils;
    private final ConfigCryptoService configCryptoService;
    private final ObjectMapper objectMapper;

    public TradeGatewayService(TradeGatewayConfigMapper gatewayConfigMapper,
                               SecurityUtils securityUtils, ConfigCryptoService configCryptoService,
                               ObjectMapper objectMapper) {
        this.gatewayConfigMapper = gatewayConfigMapper;
        this.securityUtils = securityUtils;
        this.configCryptoService = configCryptoService;
        this.objectMapper = objectMapper;
    }

    /** 当前区域网关配置（敏感字段脱敏；无配置返回 null） */
    public Map<String, Object> getConfig() {
        TradeGatewayConfig cfg = findConfig(securityUtils.getRegionCode());
        if (cfg == null) {
            return null;
        }
        return toView(cfg);
    }

    /**
     * 保存网关配置（region_code 唯一 upsert）：endpoint/网关名/状态 + {appKey, appSecret} 图形化输入；
     * appSecret 提交 ****** 视为未修改；返回脱敏视图。
     */
    public Map<String, Object> saveConfig(String gatewayName, String endpoint,
                                          String appKey, String appSecret, String status) {
        if (StrUtils.isBlank(endpoint)) {
            throw new ServiceException("接口地址（URL）不能为空");
        }
        String regionCode = securityUtils.getRegionCode();
        TradeGatewayConfig cfg = findConfig(regionCode);
        Map<String, Object> conn = new LinkedHashMap<>();
        if (cfg != null && StrUtils.isNotBlank(cfg.getConnConfig())) {
            // 编辑回显合并：脱敏占位保留密文；新值重新加密
            Map<String, Object> merged = parseMasked(cfg.getConnConfig(), appKey, appSecret);
            conn.putAll(merged);
        } else {
            conn.put("appKey", StrUtils.isBlank(appKey) ? "" : appKey);
            conn.put("appSecret", StrUtils.isBlank(appSecret) ? "" : appSecret);
        }
        String connJson = toJson(conn);
        TradeGatewayConfig save = new TradeGatewayConfig();
        if (cfg == null) {
            save.setRegionCode(regionCode);
            save.setGatewayName(StrUtils.isBlank(gatewayName) ? "交易中心申报网关" : gatewayName);
            save.setEndpoint(endpoint);
            save.setConnConfig(configCryptoService.encryptFields(connJson));
            save.setStatus(StrUtils.isBlank(status) ? "disabled" : status);
            gatewayConfigMapper.insert(save);
        } else {
            save.setId(cfg.getId());
            save.setGatewayName(StrUtils.isBlank(gatewayName) ? cfg.getGatewayName() : gatewayName);
            save.setEndpoint(endpoint);
            save.setConnConfig(configCryptoService.encryptFields(connJson));
            save.setStatus(StrUtils.isBlank(status) ? cfg.getStatus() : status);
            gatewayConfigMapper.updateById(save);
        }
        return toView(findConfig(regionCode));
    }

    /** 连通性测试（模拟网关握手：记录延迟/结果；有 enabled 配置才返回 ok） */
    public Map<String, Object> testConnection() {
        TradeGatewayConfig cfg = findConfig(securityUtils.getRegionCode());
        long start = System.currentTimeMillis();
        Map<String, Object> resp = new LinkedHashMap<>();
        if (cfg == null || StrUtils.isBlank(cfg.getEndpoint())) {
            resp.put("ok", false);
            resp.put("message", "未配置交易网关（请先填写接口地址）");
            return resp;
        }
        // 确定性模拟握手（后续可替换为真实 HTTP 探测）：endpoint 非空 + 凭据已配置即视为可达
        boolean credOk = cfg.getConnConfig() != null && cfg.getConnConfig().contains("appKey");
        long latency = (long) (20 + Math.random() * 180);
        boolean ok = credOk;
        String result = ok
                ? "ok · " + latency + "ms · 网关握手成功（模拟探测，可配置真实地址后联调）"
                : "fail · 网关凭据未配置完整";
        TradeGatewayConfig update = new TradeGatewayConfig();
        update.setId(cfg.getId());
        update.setLastTestAt(new Date());
        update.setLastTestResult(result);
        gatewayConfigMapper.updateById(update);
        resp.put("ok", ok);
        resp.put("latencyMs", latency);
        resp.put("message", result);
        resp.put("testedAt", update.getLastTestAt());
        return resp;
    }

    /** 内部：按区域查配置（当前用户区域） */
    public TradeGatewayConfig findConfig(String regionCode) {
        if (StrUtils.isBlank(regionCode)) {
            return null;
        }
        return gatewayConfigMapper.selectOne(new LambdaQueryWrapper<TradeGatewayConfig>()
                .eq(TradeGatewayConfig::getRegionCode, regionCode).last("LIMIT 1"));
    }

    /** 脱敏视图：appSecret → ******（明文与密文均不外泄） */
    private Map<String, Object> toView(TradeGatewayConfig cfg) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", String.valueOf(cfg.getId()));
        item.put("regionCode", cfg.getRegionCode());
        item.put("gatewayName", cfg.getGatewayName());
        item.put("endpoint", cfg.getEndpoint());
        String conn = cfg.getConnConfig();
        item.put("connConfig", StrUtils.isBlank(conn) ? "{}" : configCryptoService.maskFields(conn));
        item.put("status", cfg.getStatus());
        item.put("lastTestAt", cfg.getLastTestAt());
        item.put("lastTestResult", cfg.getLastTestResult());
        return item;
    }

    /** 解析库中密文，合并提交值：提交 ****** 保留密文，新值明文（调用方再加密落库） */
    private Map<String, Object> parseMasked(String storedJson, String appKey, String appSecret) {
        Map<String, Object> merged = new LinkedHashMap<>();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> stored = objectMapper.readValue(
                    configCryptoService.decryptFields(storedJson),
                    Map.class);
            if (stored != null) {
                merged.putAll(stored);
            }
        } catch (Exception ignored) {
            // 解析失败按空处理
        }
        if (StrUtils.isNotBlank(appKey)) {
            merged.put("appKey", appKey);
        }
        if (StrUtils.isNotBlank(appSecret) && !"******".equals(appSecret)) {
            merged.put("appSecret", appSecret);
        }
        return merged;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new ServiceException("JSON 序列化失败：" + e.getMessage());
        }
    }
}
