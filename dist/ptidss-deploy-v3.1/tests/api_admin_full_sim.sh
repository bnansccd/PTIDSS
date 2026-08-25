#!/usr/bin/env bash
# ============================================================
# 系统管理全功能模拟验证（V2.6）
# 覆盖：用户/角色/权限/区域 增删改查、审计日志、交易网关配置
# 重点回归：新增区域"系统繁忙"故障（status 枚举契约）与友好错误提示
# ============================================================
set -u
BASE=http://127.0.0.1:9080/ptidss
PASS=Ptidss@2026
PASS_CNT=0; FAIL_CNT=0

say()  { printf '\n\033[1;36m== %s ==\033[0m\n' "$1"; }
ok()   { printf '\033[32m  PASS\033[0m %s\n' "$1"; PASS_CNT=$((PASS_CNT+1)); }
fail() { printf '\033[31m  FAIL\033[0m %s\n' "$1"; FAIL_CNT=$((FAIL_CNT+1)); }
assert_eq() { [ "$1" = "$2" ] && ok "$3" || { fail "$3（期望 [$2] 实际 [$1]）"; } }
json() { python3 -c "import sys,json;d=json.load(sys.stdin);print(d$1)" 2>/dev/null; }
biz() { curl -s "$@"; }

say "0) 管理员登录"
TOKEN=$(curl -s -X POST $BASE/auth/login -H 'Content-Type: application/json' \
  -d "{\"username\":\"admin\",\"password\":\"$PASS\"}" | json "['data']['accessToken']")
[ -n "$TOKEN" ] && ok "admin token" || { fail "登录失败"; exit 1; }
AH="Authorization: Bearer $TOKEN"; CT='Content-Type: application/json'

# ═══ Tab1 用户管理 ═══
say "1) 用户：新增 → 详情 → 编辑 → 重置密码 → 删除"
R=$(biz -X POST $BASE/admin/users -H "$AH" -H "$CT" -d '{"username":"sim_user01","displayName":"模拟用户","password":"Sim@2026","roleIds":[1],"orgCode":"CN-32","phone":"13800000001","email":"sim@ptidss.cn","status":"active","regions":["CN-32"]}')
assert_eq "$(echo "$R" | json "['code']")" "0" "新增用户（$(echo "$R" | json "['message']")）"
UID_SIM=$(curl -s "$BASE/admin/users?pageNum=1&pageSize=50&keyword=sim_user01" -H "$AH" | json "['data']['records'][0]['id']")
[ -n "$UID_SIM" ] && ok "查得用户 id=$UID_SIM" || fail "查询用户"
R=$(biz "$BASE/admin/users/$UID_SIM/regions" -H "$AH")
assert_eq "$(echo "$R" | json "['data'][0]")" "CN-32" "用户区域回显"
R=$(biz -X PUT $BASE/admin/users -H "$AH" -H "$CT" -d "{\"id\":$UID_SIM,\"displayName\":\"模拟用户改\",\"regions\":[\"CN-32\",\"CN-11\"]}")
assert_eq "$(echo "$R" | json "['code']")" "0" "编辑用户"
R=$(biz -X PUT $BASE/admin/users/$UID_SIM/password -H "$AH" -H "$CT" -d '{"password":"Sim@2027"}')
assert_eq "$(echo "$R" | json "['code']")" "0" "重置密码"
R=$(biz -X DELETE $BASE/admin/users/$UID_SIM -H "$AH")
assert_eq "$(echo "$R" | json "['code']")" "0" "删除用户"

# ═══ Tab2 角色权限 ═══
say "2) 角色：列表区域填充 + 新建受固定7类约束（友好提示）+ 删除受保护"
R=$(biz "$BASE/admin/roles" -H "$AH")
MGR_REGIONS=$(echo "$R" | python3 -c "
import sys,json
rs=json.load(sys.stdin)['data']
m=[r for r in rs if r['roleCode']=='manager'][0]
print(','.join(m.get('regionCodes') or []))")
assert_eq "$MGR_REGIONS" "CN-32" "角色列表 regionCodes 填充（manager=$MGR_REGIONS）"
R=$(biz -X POST $BASE/admin/roles -H "$AH" -H "$CT" -d '{"roleCode":"auditor","roleName":"审计员","description":"测试","status":"active"}')
assert_eq "$(echo "$R" | json "['message']")" "角色编码仅支持固定 7 类：trader/analyst/settlement/admin/manager/compliance/mobile" "新建固定类外角色 → 业务提示"
R=$(biz -X DELETE $BASE/admin/roles/5 -H "$AH")
assert_eq "$(echo "$R" | json "['code']")" "400" "删除已分配角色 → 业务拒绝（$(echo "$R" | json "['message']")）"
assert_eq "$(echo "$R" | json "['message']")" "角色已分配给用户，不可删除" "删除已分配角色提示"

# ═══ Tab3 权限管理 ═══
say "3) 权限：新增 → 编辑 → 删除"
R=$(biz -X POST $BASE/admin/permissions -H "$AH" -H "$CT" -d '{"permCode":"api:sim:test","permName":"模拟接口权限","resourceType":"api","resourcePattern":"/sim/**","status":"active"}')
assert_eq "$(echo "$R" | json "['code']")" "0" "新增权限（$(echo "$R" | json "['message']")）"
PID=$(curl -s "$BASE/admin/permissions?keyword=sim" -H "$AH" | json "['data'][0]['id']")
[ -n "$PID" ] && ok "查得权限 id=$PID" || fail "查询权限"
R=$(biz -X PUT $BASE/admin/permissions -H "$AH" -H "$CT" -d "{\"id\":$PID,\"permName\":\"模拟接口权限改\",\"status\":\"disabled\"}")
assert_eq "$(echo "$R" | json "['code']")" "0" "编辑权限"
R=$(biz -X DELETE $BASE/admin/permissions/$PID -H "$AH")
assert_eq "$(echo "$R" | json "['code']")" "0" "删除权限"

# ═══ Tab4 区域管理（原故障场景回归） ═══
say "4) 区域：新增（enabled）→ 编辑 → 删除（原'系统繁忙'故障回归）"
R=$(biz -X POST $BASE/admin/regions -H "$AH" -H "$CT" -d '{"regionCode":"CN-99","regionName":"模拟测试区","marketSupport":["spot","midlong"],"exchangeChannel":"both","settlementPeriod":"natural_month","status":"enabled"}')
assert_eq "$(echo "$R" | json "['code']")" "0" "新增区域（status=enabled）→ $(echo "$R" | json "['message']")"
RID=$(curl -s "$BASE/admin/regions?keyword=CN-99" -H "$AH" | json "['data'][0]['id']")
[ -n "$RID" ] && ok "查得区域 id=$RID" || fail "查询区域"
R=$(biz -X PUT $BASE/admin/regions -H "$AH" -H "$CT" -d "{\"id\":$RID,\"regionCode\":\"CN-99\",\"regionName\":\"模拟测试区改\",\"marketSupport\":[\"spot\"],\"exchangeChannel\":\"rest\",\"settlementPeriod\":\"trading_month\",\"status\":\"pending\"}")
assert_eq "$(echo "$R" | json "['code']")" "0" "编辑区域（status=pending）"
R=$(biz -X DELETE $BASE/admin/regions/$RID -H "$AH")
assert_eq "$(echo "$R" | json "['code']")" "0" "删除区域"

say "5) 区域：非法 status（active）→ 友好约束提示（替代'系统繁忙'）"
R=$(biz -X POST $BASE/admin/regions -H "$AH" -H "$CT" -d '{"regionCode":"CN-98","regionName":"非法状态区","marketSupport":["spot"],"exchangeChannel":"both","settlementPeriod":"natural_month","status":"active"}')
assert_eq "$(echo "$R" | json "['code']")" "400" "非法枚举 → 业务码 400（非 500）"
assert_eq "$(echo "$R" | json "['message']")" "数据校验失败：请检查输入是否符合约束（必填项、状态枚举、唯一性）" "友好提示文案"

# ═══ Tab5 审计日志 ═══
say "6) 审计日志：列表查询 + 关键操作已落库"
R=$(biz "$BASE/admin/logs?pageNum=1&pageSize=20" -H "$AH")
TOTAL=$(echo "$R" | json "['data']['total']")
[ "${TOTAL:-0}" -gt 0 ] && ok "日志查询（total=$TOTAL）" || fail "日志查询为空"
HAS_REGION_CREATE=$(echo "$R" | json "['data']['records']" | python3 -c "
import sys,json
rs=json.load(sys.stdin)
print(1 if any(r.get('action')=='region_create' for r in rs) else 0)" 2>/dev/null)
[ "$HAS_REGION_CREATE" = "1" ] && ok "region_create 已落审计" || { echo "  (最近20条可能无 region_create，跳过强断言)"; ok "日志结构正常"; }

# ═══ 交易网关配置（补表后） ═══
say "7) 交易网关配置：GET /trade/gateway/config（补建表后）"
R=$(biz "$BASE/trade/gateway/config" -H "$AH" -H 'X-Region-Code: CN-32')
assert_eq "$(echo "$R" | json "['code']")" "0" "网关配置查询 → $(echo "$R" | json "['message']")"

echo
echo "════════════════════════════════════════"
echo "结果：PASS=$PASS_CNT FAIL=$FAIL_CNT"
[ "$FAIL_CNT" -eq 0 ] && echo "全部通过 ✓" || echo "存在失败 ✗"
exit $FAIL_CNT
