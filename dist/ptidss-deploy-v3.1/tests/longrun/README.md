# PTIDSS 长期验证（90 天）执行说明

> V2.5 遗留建议②落地：按《V2.4市场化省份接入与长期验证方案》§2/§3 指标与节奏，
> 将日/周/月/季验证固化为可调度脚本，复用系统接口（采集状态/数据质量/模型任务报告对标）。

## 脚本清单

| 脚本 | 周期 | 验证指标（目标值） | 输出 |
| --- | --- | --- | --- |
| `daily_validation.py` | 每日 | 采集成功率 ≥98%、采集时效达标率 ≥95%、数据完整性 ≥99%、质量三率 ≥95% | reports/daily_YYYYMMDD.json |
| `weekly_validation.py` | 每周 | 情报源失败率 ≤2%、质量三率 ≥95%、情报入库量趋势 | reports/weekly_YYYYMMDD.json |
| `monthly_validation.py` | 每月 | 算法任务成功率 ≥99%、平均 MAPE ≤8%、方向准确率 ≥80%、对标无恶化 | reports/monthly_YYYYMM.json |
| `quarterly_validation.py` | 每季 | 启用区域 ≥6、现货省份 ≥6、情报源健康率 ≥95%、模型/算法规模基线 | reports/quarterly_YYYYQn.json |

## 运行方式

```bash
# 测试/内网直连（captcha=false 或验证账号）
python3 daily_validation.py            # 输出摘要 + JSON 报告
python3 daily_validation.py --exit-code  # 未达标退出码 1（供调度告警联动）

# 生产（captcha 开启）：配置专用验证账号或经 nginx 内网访问
PTIDSS_BASE=http://127.0.0.1:9080/ptidss PTIDSS_USER=admin PTIDSS_PWD=****** python3 daily_validation.py
```

## 调度示例（crontab）

```cron
# 每日 01:05 日验证（未达标邮件告警）
5 1 * * * cd /opt/ptidss/tests/longrun && /usr/bin/python3 daily_validation.py --exit-code || echo "PTIDSS 日验证未达标" | mail -s "PTIDSS 日验证 FAIL" ops@example.com
# 每周一 02:05 周验证
5 2 * * 1 cd /opt/ptidss/tests/longrun && /usr/bin/python3 weekly_validation.py
# 每月 1 日 03:05 月验证（模型 MAPE/方向准确率/对标）
5 3 1 * * cd /opt/ptidss/tests/longrun && /usr/bin/python3 monthly_validation.py
# 每季 1 日 04:05 季验证（全面复核基线）
5 4 1 1,4,7,10 * cd /opt/ptidss/tests/longrun && /usr/bin/python3 quarterly_validation.py
```

## 说明

1. 依赖：`python3 + requests`（`pip install requests`）。
2. 报告留存于 `reports/`，建议纳入备份；连续 FAIL 时按《方案》§5 处置（检查接口配置/联系数据方）。
3. 模型月验证复用需求 8 任务报告（process_steps/result_json/compare_json 对标），无需额外埋点。
4. 生产切换真实行情：将情报源台账 `conn_config.mock` 置 `false` 并配置各省交易中心真实 endpoint，
   系统自动走"HTTP 拉取 → 重试退避 → fallbackUrl 降级 → 状态留痕（≥10 次连续失败自动停用）"链路。
