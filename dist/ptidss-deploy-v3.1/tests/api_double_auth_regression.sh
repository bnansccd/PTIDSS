#!/usr/bin/env bash
# ============================================================
# 双重授权回归（评审决议⑤）：有效区域 = 用户授权区域 ∩ 角色授权区域
# 场景：manager01 用户区域 CN-11+CN-32；manager 角色区域 CN-32
#   1) 交集裁剪：登录 regions 应为 [CN-32]（CN-11 被角色区域裁掉）
#   2) 越权拒绝：X-Region-Code: CN-11 → 401
#   3) 正常放行：X-Region-Code: CN-32 → 200；缺省头 → 200
#   4) 向后兼容：trader01（角色未配置区域）→ 用户区域生效
#   5) 会话失效：角色区域变更后旧 token 立即失效（401）
# ============================================================
set -u
BASE=http://127.0.0.1:9080/ptidss
PASS=Ptidss@2026
BUSI=/flow/instances?scope=todo        # manager01 有权限的业务接口
PASS_CNT=0; FAIL_CNT=0

say()  { printf '\n\033[1;36m== %s ==\033[0m\n' "$1"; }
ok()   { printf '\033[32m  PASS\033[0m %s\n' "$1"; PASS_CNT=$((PASS_CNT+1)); }
fail() { printf '\033[31m  FAIL\033[0m %s\n' "$1"; FAIL_CNT=$((FAIL_CNT+1)); }
assert_eq() { [ "$1" = "$2" ] && ok "$3" || { fail "$3（期望 [$2] 实际 [$1]）"; } }
# 业务码：0=成功，14001=未授权区域（全局异常处理器统一 HTTP 200 + 业务码）
biz_code() { curl -s "$@" | json "['code']"; }

json() { python3 -c "import sys,json;d=json.load(sys.stdin);print(d$1)" 2>/dev/null; }

say "0) 管理员登录"
ADMIN_TOKEN=$(curl -s -X POST $BASE/auth/login -H 'Content-Type: application/json' \
  -d "{\"username\":\"admin\",\"password\":\"$PASS\"}" | json "['data']['accessToken']")
[ -n "$ADMIN_TOKEN" ] && ok "admin token 获取" || { fail "admin 登录失败"; exit 1; }
AH="Authorization: Bearer $ADMIN_TOKEN"

say "1) 准备：用户 manager01 区域 = CN-11+CN-32；角色 manager 区域 = CN-32"
R=$(curl -s -X PUT $BASE/admin/users -H "$AH" -H 'Content-Type: application/json' \
  -d '{"id":4,"regions":["CN-11","CN-32"]}'); echo "  PUT user regions → $(echo "$R" | json "['message']")"
R=$(curl -s -X PUT $BASE/admin/roles -H "$AH" -H 'Content-Type: application/json' \
  -d '{"id":5,"roleCode":"manager","regionCodes":["CN-32"]}'); echo "  PUT role regions → $(echo "$R" | json "['message']")"

say "2) 交集裁剪：manager01 登录 regions 应为 [CN-32]"
M_LOGIN=$(curl -s -X POST $BASE/auth/login -H 'Content-Type: application/json' \
  -d "{\"username\":\"manager01\",\"password\":\"$PASS\"}")
M_TOKEN=$(echo "$M_LOGIN" | json "['data']['accessToken']")
M_REGIONS=$(echo "$M_LOGIN" | json "['data']['regions']")
assert_eq "$M_REGIONS" "['CN-32']" "登录区域 = 用户区域 ∩ 角色区域（CN-11+CN-32 ∩ CN-32 = CN-32）"
MH="Authorization: Bearer $M_TOKEN"

say "3) 越权拒绝：X-Region-Code: CN-11 → 业务码 14001"
C=$(biz_code $BASE$BUSI -H "$MH" -H 'X-Region-Code: CN-11')
assert_eq "$C" "14001" "未授权区域 CN-11 被拒绝（code=$C）"

say "4) 正常放行：CN-32 → 业务码 0；缺省头 → 默认区域"
C=$(biz_code $BASE$BUSI -H "$MH" -H 'X-Region-Code: CN-32')
assert_eq "$C" "0" "授权区域 CN-32 放行（code=$C）"
C=$(biz_code $BASE$BUSI -H "$MH")
assert_eq "$C" "0" "缺省区域头取首个授权区域（code=$C）"

say "5) 向后兼容：trader01（trader/mobile 角色未配置区域）→ 用户区域 CN-32 生效"
T_LOGIN=$(curl -s -X POST $BASE/auth/login -H 'Content-Type: application/json' \
  -d "{\"username\":\"trader01\",\"password\":\"$PASS\"}")
T_REGIONS=$(echo "$T_LOGIN" | json "['data']['regions']")
assert_eq "$T_REGIONS" "['CN-32']" "角色未配置区域时以用户区域为准"
T_TOKEN=$(echo "$T_LOGIN" | json "['data']['accessToken']")
C=$(biz_code $BASE$BUSI -H "Authorization: Bearer $T_TOKEN" -H 'X-Region-Code: CN-32')
assert_eq "$C" "0" "trader01 用户区域请求放行（code=$C）"

say "6) 会话失效：角色区域变更后旧 token 立即失效"
R=$(curl -s -X PUT $BASE/admin/roles -H "$AH" -H 'Content-Type: application/json' \
  -d '{"id":5,"roleCode":"manager","regionCodes":["CN-32","CN-11"]}')
echo "  PUT role regions += CN-11 → $(echo "$R" | json "['message']")"
C=$(biz_code $BASE$BUSI -H "$MH")
assert_eq "$C" "14001" "角色区域变更后旧会话失效（code=$C）"
M2=$(curl -s -X POST $BASE/auth/login -H 'Content-Type: application/json' \
  -d "{\"username\":\"manager01\",\"password\":\"$PASS\"}" | json "['data']['regions']")
assert_eq "$M2" "['CN-11', 'CN-32']" "重新登录后交集 = CN-11+CN-32"

say "7) 清理：用户 manager01 区域恢复 CN-32；角色 manager 区域恢复 CN-32"
curl -s -X PUT $BASE/admin/users -H "$AH" -H 'Content-Type: application/json' \
  -d '{"id":4,"regions":["CN-32"]}' > /dev/null
curl -s -X PUT $BASE/admin/roles -H "$AH" -H 'Content-Type: application/json' \
  -d '{"id":5,"roleCode":"manager","regionCodes":["CN-32"]}' > /dev/null
ok "数据已恢复"

echo
echo "════════════════════════════════════════"
echo "结果：PASS=$PASS_CNT FAIL=$FAIL_CNT"
[ "$FAIL_CNT" -eq 0 ] && echo "全部通过 ✓" || echo "存在失败 ✗"
exit $FAIL_CNT
