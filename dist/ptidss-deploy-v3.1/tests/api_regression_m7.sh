#!/bin/bash
# M7 接口回归：新接口优先 + 小程序依赖接口抽查
# 用法：bash tests/api_regression_m7.sh
BASE="http://localhost:9080/ptidss"
R="X-Region-Code: CN-32"
TMP="/home/odoo/workspace/PTIDSS/tests/.m7_r.json"
PASS=0; FAIL=0
ck() { # ck <name> <http_code> <expected>
  if [ "$2" = "$3" ]; then echo "PASS $1 (http=$2)"; PASS=$((PASS+1)); else echo "FAIL $1 (http=$2, want $3)"; FAIL=$((FAIL+1)); fi
}

# 1. 登录
LOGIN=$(curl -s -X POST "$BASE/auth/login" -H "Content-Type: application/json" -d '{"username":"admin","password":"Ptidss@2026"}')
TOKEN=$(echo "$LOGIN" | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
if [ -z "$TOKEN" ]; then echo "FATAL 登录失败: $LOGIN"; exit 1; fi
echo "PASS 登录 (token len=${#TOKEN})"; PASS=$((PASS+1))
A="Authorization: Bearer $TOKEN"

# 2. M7 新接口：流程实例列表三 scope
for sc in todo started all; do
  CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/flow/instances?scope=$sc&pageNo=1&pageSize=5" -H "$A" -H "$R")
  TOTAL=$(sed -n 's/.*"total":\([0-9]*\).*/\1/p' "$TMP" | head -1)
  ck "flow/instances scope=$sc" "$CODE" 200
  echo "     -> total=${TOTAL:-0}"
done

# 3. M7 新接口：决策会话列表
CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/decision/sessions?pageNo=1&pageSize=5" -H "$A" -H "$R")
ck "decision/sessions" "$CODE" 200
echo "     -> $(head -c 160 "$TMP")"

# 4. 小程序依赖接口抽查
CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/market/price/spot?marketType=intra_province&stage=day_ahead" -H "$A" -H "$R")
ck "market/price/spot" "$CODE" 200
CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/market/price/midlong?variety=weekly" -H "$A" -H "$R")
ck "market/price/midlong" "$CODE" 200
CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/market/supply-demand" -H "$A" -H "$R")
ck "market/supply-demand" "$CODE" 200
CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/market/heatmap?startDate=$(date +%F -d '6 days ago')&endDate=$(date +%F)" -H "$A" -H "$R")
ck "market/heatmap" "$CODE" 200
echo "     -> dates=$(sed -n 's/.*"dates":\[\([^]]*\)\].*/\1/p' "$TMP" | head -c 120)"
CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/message/list?pageNo=1&pageSize=5" -H "$A" -H "$R")
ck "message/list" "$CODE" 200
CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/review/reports?pageNo=1&pageSize=5" -H "$A" -H "$R")
ck "review/reports" "$CODE" 200
CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/auth/current" -H "$A" -H "$R")
ck "auth/current" "$CODE" 200

# 5. 流程闭环：start -> detail -> advance
START=$(curl -s -X POST "$BASE/flow/start" -H "Content-Type: application/json" -H "$A" -H "$R" -d '{"processKey":"decision_confirm","variables":{"source":"regression"}}')
IID=$(echo "$START" | sed -n 's/.*"instanceId":"\([^"]*\)".*/\1/p')
if [ -n "$IID" ]; then
  echo "PASS flow/start (instanceId=$IID)"; PASS=$((PASS+1))
  CODE=$(curl -s -o "$TMP" -w "%{http_code}" "$BASE/flow/instances/$IID" -H "$A" -H "$R")
  ck "flow/instances/{id}" "$CODE" 200
  CODE=$(curl -s -o "$TMP" -w "%{http_code}" -X POST "$BASE/flow/instances/$IID/advance" -H "Content-Type: application/json" -H "$A" -H "$R" -d '{"action":"approve","comment":"M7 回归通过"}')
  ck "flow/instances/{id}/advance" "$CODE" 200
  echo "     -> $(head -c 120 "$TMP")"
else
  echo "FAIL flow/start (无 instanceId): $START"; FAIL=$((FAIL+1))
fi

rm -f "$TMP"
echo "==== M7 回归结果: PASS=$PASS FAIL=$FAIL ===="
