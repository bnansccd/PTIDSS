
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import ="cn.com.scca.signgw.api.SccaGwSDK" %>
<%@ page import ="org.codehaus.jackson.map.ObjectMapper" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
String toSign = request.getParameter("txtToSign");
String signedData = request.getParameter("Signature");			
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>证书登陆验证</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<script src="js/jquery.min.js" type="text/javascript"></script>
	<script type="text/javascript">

	
	</script>
	
</head>
<body>
<%
	out.println("<br/>");
	out.println("<br/>");
	out.println("<br/>");
	toSign = toSign == null ? "" : toSign;
	signedData = signedData == null ? "" : signedData;
			
	out.println("签名原文:"+toSign+ "<br/>");
	out.println("签名值:" + "<br/>" +signedData+ "<br/>");
	out.println("<br/>");

	//初始化接口地址

	// 钜安云基础地址
	String baseUrl = "http://testmicrosrv.scca.com.cn:9527/signgw-service/";
//	String baseUrl = "http://microsrv.scca.com.cn:10010/signgw-service/";
	// 钜安云分配 app_id
	String appId = "SCCA1080447279846776833";
	// 钜安云分配 app_secret
	String appSecret = "56b1047431474f0ab672f6915aff6e8e";


	// 初始化
	SccaGwSDK.init(baseUrl,appId,appSecret);
	

	
	ObjectMapper objectMapper = new ObjectMapper();

				
	//验证
		String projectId = "123";
		String opType = "系统登陆"; 
		String reqId = "1"; 
		
		String result = SccaGwSDK.certLogin(projectId, toSign, signedData,  reqId);
		out.println("登陆签名验证返回数据如下:"+ "<br/>");
		out.println(result+ "<br/><br/><br/>");
	
		Map map = objectMapper.readValue(result, Map.class);
		
		
	
	
		if ( "200".equals(map.get("code")) ) {
			
			out.println("<b>证书登录验证成功：</b>"+ "<br/>");
			
			out.println("证书信息为:"+map.get("data")+ "<br/>");
			
		}else {
			
			out.println("<b>证书登录验证失败</b>"+ "<br/>");
			
			out.println("错误信息为:"+map.get("message")+ "<br/>");

			
			
		}
		
	

%>
		
		
</body>
</html>

