var isConnect = false;
var host = "wss://localhost:36171";
var reconnetTime = 10;
var timeConnect = 0;
var retValue;

var nullStr = 'null';
var SOR_TimeoutErr = 0x0B000101;
var SOR_NotSupportYetErr = 0x0B000002;
var BROWSER_TYPE_NPAPI = 0;
var BROWSER_TYPE_WSS = 1;
var BROWSER_TYPE_HTTPS = 2;

var webSocket = "";
var plugin = "";
var browserType = BROWSER_TYPE_NPAPI;

var title = '需要验证您的登录密码';
var placeholder = '请输入登录密码';
var bpluginvalid = false;
(function() {
    window.elCustomPrompt = elCustomPrompt;
    browserType = getBrowser();
    if(browserType === BROWSER_TYPE_NPAPI) {
        plugin = plugin0;
    }else if(browserType === BROWSER_TYPE_WSS){
        initWebSocket();
    }
})()

//根据浏览器版本控件类型 0 NPAPI, 1 WSS, 2 HTTPS
export function getBrowser() {
    var browserType = BROWSER_TYPE_NPAPI;
    var UserAgent = navigator.userAgent.toLowerCase();
    var browserArray = {
        IE: window.ActiveXObject || "ActiveXObject" in window,
// IE
        Chrome: UserAgent.indexOf('chrome') > -1 && UserAgent.indexOf('safari') > -1,
// Chrome浏览器
        Firefox: UserAgent.indexOf('firefox') > -1,
// 火狐浏览器
        Opera: UserAgent.indexOf('opera') > -1,
// Opera浏览器
        Safari: UserAgent.indexOf('safari') > -1 && UserAgent.indexOf('chrome') === -1,
// safari浏览器
        Edge: UserAgent.indexOf('edge') > -1 && UserAgent.indexOf('edg') > -1,
// Edge浏览器
        QQBrowser: /qqbrowser/.test(UserAgent),
// QQ浏览器
    };
    for (var i in browserArray) {
        if (browserArray[i]) {
            var versions = '';
            switch (i) {
                case 'IE':
                    browserType = BROWSER_TYPE_NPAPI;
                    break;
                case 'Chrome':
                    versions = UserAgent.match(/chrome\/([\d.]+)/)[1].split('.')[0];
                    if(versions > 55) {
                        browserType = BROWSER_TYPE_WSS;
                    }
                    break;
                case 'Firefox':
                    versions = UserAgent.match(/firefox\/([\d.]+)/)[1];
                    if(versions > 52) {
                        browserType = BROWSER_TYPE_WSS;
                    }
                    break;
                case 'Opera':
                    versions = UserAgent.match(/opera\/([\d.]+)/)[1];
                    if(versions > 42) {
                        browserType = BROWSER_TYPE_WSS;
                    }
                    break;
                case 'Safari':
                    versions = UserAgent.match(/version\/([\d.]+)/)[1];
                    if(versions > 10.1) {
                        browserType = BROWSER_TYPE_WSS;
                    }
                    break;
                case 'Edge':
                    versions = UserAgent.match(/edge\/([\d.]+)/)[1];
                    if(versions > 14) {
                        browserType = BROWSER_TYPE_WSS;
                    }
                    break;
                case 'QQBrowser':
                    browserType = BROWSER_TYPE_WSS;
                    versions = UserAgent.match(/qqbrowser\/([\d.]+)/)[1];
					WEB_SOCKET_SWF_LOCATION = "wss.swf";
                    break;
            }
        }
    }
    return browserType;
}

export function plugin0()
{
    return document.getElementById('plugin0');
}

//初始化websocket
export function initWebSocket()
{
    try {
        if(webSocket == null || typeof webSocket !== webSocket)
        {
            webSocket = new WebSocket(host);
            webSocket.onopen = doOpen;
            webSocket.onerror = doError;
            webSocket.onclose = doClose;
        }else{
            doReconnet();
        }
    }catch (e) {
       console.log(e);
    }

}

export function doOpen()
{
    isConnect = true;
}

export function doError()
{
    console.log("connect error");
    isConnect = false;
    doReconnet();
}

export function doClose()
{
    isConnect = false;
}

export function doReconnet()
{
    if(!isConnect && reconnetTime > 0 && timeConnect < 2)
    {
        reconnetTime --;
        timeConnect ++;
        var timer = setTimeout(function() {
              if(webSocket.readyState === 2 || webSocket.readyState === 3) {
                 initWebSocket();
               }else if(webSocket.readyState === 1)
                {
                   clearTimeout(timer);
                }
        },2000)
    }else{
        console.log("timeout");
    }
}

/**
 * @description elementUI风格自定义propmt封装
 * @param { String } title  标题propmt第一个参数
 * @param { String } placeholder input的placeholder
 *   - 点击确认后，执行的函数，参数为当前值，
 *   - return格式:
 *     { result: true, msg: '成功'}
 *     { result: false, msg: '自定义错误信息' }
 */

export function elCustomPrompt(containerName, title, placeholder) {
    return new Promise(function(resolve) {
        var errMsg = '' // 错误信息

        // 创建dom并挂载
        var domStr = '<!--遮罩-->'+
            '<div class="v-modal" tabindex="0" style="z-index:2054;position: fixed;left: 0px;top: 0px;width: 100%;height: 100%;opacity: 0.5;background: rgb(0, 0, 0);"></div>'+
            '<!--弹窗_start--><div id="elCustomPromptMain" tabindex="-1" role="dialog" class="el-message-box__wrapper" style="z-index:2055;position: fixed;top: 0px;bottom: 0px;left: 0px;right: 0px;text-align: center;">'+
            '<!--消息盒子_Start--><div class="el-message-box" id="elCustomPromptMessageBox" style="display: inline-block;width: 420px;margin-top:100px;padding-bottom: 10px;vertical-align: middle;background-color: rgb(255, 255, 255);font-size: 18px;box-shadow: rgba(0, 0, 0, 0.1) 0px 2px 12px 0px;text-align: left;backface-visibility: hidden;border-radius: 4px;border-width: 1px;border-style: solid;border-color: rgb(235, 238, 245);border-image: initial;overflow: hidden;">'+
            '<!--顶部title以及关闭按钮--><div class="el-message-box__header" style="position: relative;padding: 15px 15px 10px;">'+
            '<div class="el-message-box__title" style="padding-left: 0px;margin-bottom: 0px;font-size: 18px;line-height: 1;color: rgb(48, 49, 51);"><span>'+title+'</span></div>'+
            '</div>'+
            '<!--内容--><div class="el-message-box__content" style="position: relative;color: rgb(96, 98, 102);font-size: 14px;padding: 10px 15px;">'+
            '<div class="el-message-box__input" style="padding-top: 15px;">'+
            '<div class="el-input">'+
            '<input type="password" id="elCustomPromptInput" autocomplete="off" placeholder="'+placeholder+'" class="el-input__inner" style="-webkit-appearance: none;background-color: rgb(255, 255, 255);background-image: none;box-sizing: border-box;color: rgb(96, 98, 102);display: inline-block;font-size: inherit;height: 40px;line-height: 40px;width: 100%;border-radius: 4px;border-width: 1px;border-style: solid;border-top-style: solid;border-right-style: solid;border-bottom-style: solid;border-left-style: solid;border-color: rgb(220, 223, 230);border-image: initial;outline: none;padding: 0px 15px;transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1) 0s;">'+
            '</div>'+
            '<div class="el-message-box__errormsg" id="elCustomPromptErrMsgDiv" style="visibility:hidden;color: rgb(245, 108, 108);font-size: 12px;min-height: 18px;margin:5px;">'+errMsg+'</div>'+
            '</div>'+
            '</div>'+
            '<!--底部按钮--><div class="el-message-box__btns" style="text-align: right;padding: 5px 15px 0px;">'+
            '<button type="button" class="el-button el-button--default el-button--small" id="elCustomPromptCancelBtn" style="display: inline-block;line-height: 1;white-space: nowrap;cursor: pointer;color: rgb(96, 98, 102);-webkit-appearance: none;text-align: center;box-sizing: border-box;font-weight: 500;user-select: none;font-size: 12px;background: rgb(255, 255, 255);border-width: 1px;border-style: solid;border-color: rgb(220, 223, 230);border-image: initial;outline: none;margin: 0px;transition: all 0.1s ease 0s;border-radius: 4px; padding: 9px 15px;"><span>取消</span></button>'+
            '<button type="button" class="el-button el-button--default el-button--small el-button--primary" id="elCustomPromptOkBtn" disabled="disabled" style="display: inline-block;line-height: 1;white-space: nowrap;cursor: pointer;color: rgb(96, 98, 102);-webkit-appearance: none;text-align: center;box-sizing: border-box;font-weight: 500;user-select: none;font-size: 12px;background: rgb(255, 255, 255);border-width: 1px;border-style: solid;border-color: rgb(220, 223, 230);border-image: initial;outline: none;margin: 0px;margin-left: 5px;transition: all 0.1s ease 0s;padding: 9px 15px;border-radius: 4px;color: rgb(255, 255, 255);background-color: rgb(64, 158, 255);border-color: rgb(64, 158, 255);background: rgb(102, 177, 255);cursor: not-allowed;"><span>确定</span></button>'+
            '</div>'+
            '</div><!--消息盒子_end-->'+
            '</div><!--弹窗_end-->'
        var div = document.createElement('div')
        div.setAttribute('id', 'elCustomPromptDiv')
        div.innerHTML = domStr
        document.body.appendChild(div)

        // 移除弹窗框
        function removeDiv() {
            document.body.removeChild(div)
        }
        // 绑定事件
        var elCustomPromptErrMsgDiv = document.getElementById('elCustomPromptErrMsgDiv'),
            elCustomPromptCancelBtn = document.getElementById('elCustomPromptCancelBtn'),
            elCustomPromptOkBtn = document.getElementById('elCustomPromptOkBtn'),
            elCustomPromptInput = document.getElementById('elCustomPromptInput'),
            elCustomPromptMain = document.getElementById('elCustomPromptMain')
        elCustomPromptMessageBox = document.getElementById('elCustomPromptMessageBox')

        // 弹窗取消事件
        elCustomPromptCancelBtn.onclick = function() {
            removeDiv();
			resolve("cancel");
        }

        // 点击确定后的操作
        elCustomPromptOkBtn.onclick = function() {
            // 再次校验
            var tempResult = validateFunc(elCustomPromptInput.value)
            if (!tempResult.result) {
                elCustomPromptErrMsgDiv.style.visibility = 'visible'
                elCustomPromptErrMsgDiv.innerHTML = tempResult.msg
                elCustomPromptOkBtn.setAttribute('disabled', 'disabled')
                return;
            }
            return loginPrompt(containerName, elCustomPromptInput.value, function (res) {
				if(res !== "false") {
					resolve(res);
				}
            });
        };

        // 输入事件
        elCustomPromptInput.oninput = function(e) {
            // 校验并显示信息
            var tempResult = validateFunc(elCustomPromptInput.value)
            if (tempResult.result) {
                // 校验成功
                elCustomPromptErrMsgDiv.style.visibility = 'hidden'
                elCustomPromptErrMsgDiv.innerHTML = ''
                elCustomPromptOkBtn.removeAttribute('disabled');
                elCustomPromptOkBtn.style.cursor = 'auto';
                elCustomPromptOkBtn.style.background = 'rgb(64, 158, 255)';
            } else {
                // 校验失败
                elCustomPromptErrMsgDiv.style.visibility = 'visible'
                elCustomPromptErrMsgDiv.innerHTML = tempResult.msg
                elCustomPromptOkBtn.setAttribute('disabled', 'disabled')
                elCustomPromptOkBtn.style.cursor = 'not-allowed';
                elCustomPromptOkBtn.style.background = 'rgb(102, 177, 255)';
            }
        }
    })
}

// 校验函数
export function validateFunc(text) {
    var MSG_FORMAT_ERROR = '格式错误，请输入6-16位字符'
    var MSG_TAG_EXIST = '该客户属性已存在，无法添加'
    var MSG_NOT_EMPTY = '内容不能为空'

    // 格式校验
    if (text === '') {
        return { result: false, msg: MSG_NOT_EMPTY }
    }
    if (!/^[[\x21-\x7E]{6,16}$/.test(text)) {
        return { result: false, msg: MSG_FORMAT_ERROR }
    }

    // 是否有重复
    return { result: true, msg: '成功' }
}

// 登录框登录
function loginPrompt(containerName, password, callback) {
    // 执行后续操作
    var msg = ""
    if (password !== null && password !== "") {
        return new SOF_Login(containerName, password).then(function(retValue){
            if(!retValue) {
                return new SOF_GetPinRetryCount(containerName).then(function(res){
                    elCustomPromptErrMsgDiv.style.visibility = 'visible';
                    elCustomPromptErrMsgDiv.innerHTML = "登录失败！\n剩余口令重试次数：" + res + "次";
                    elCustomPromptOkBtn.setAttribute('disabled', 'disabled');
                    elCustomPromptInput.value = '';
                    callback("false");
                });
            }else{
                var divId = document.getElementById('elCustomPromptDiv')
                divId.parentNode.removeChild(divId);
                callback(retValue);
            }
        });
    }else{
        callback("false");
    }
}

//显示登录框
export function showLoginPrompt(containerName) {
    return elCustomPrompt(containerName, title, placeholder);
}

export function doSend(message)
{
    if(webSocket !== null){
        webSocket.send(message);
    }
}

export function getMessageTemp(obj) {
    if(isConnect){
        doSend(obj);
    }else{
        setTimeout(function()
        {
            doSend(obj);
        }, 300);
    }
    return new Promise(function(resolve) {
        webSocket.onmessage = function (event) {
            var jdata = JSON.parse(event.data);
            resolve(jdata.retValue);
        };
    });
}

export function windowShowModalDialog(cName)
{
    var resCount = "";
    var i = 10;
    var isLogin = false;
    for (i = 10; i > 0; i--) {
        password = window.showModalDialog("loginModal.html", resCount, "dialogWidth=420px;dialogHeight=192px");
        if(password === undefined) {
            isLogin = false;
            break;
        }
        isLogin = SOF_Login(cName, password);
        if(isLogin == "true") {
            isLogin = true;
            break;
        }else {
            resCount = SOF_GetPinRetryCount(cName);
            i = resCount;
        }
    }
    return isLogin;
}

export function getMessagePlugin(obj, callback) {
    var password;
    var isLogin = false;
    var outObjstr = plugin().call_function(obj);
    var response = JSON.parse(outObjstr);
    var resCount = "";
    if(response.retCode === 184550659) {
        var cName = decodeURIComponent((isNaN(JSON.parse(obj).params[0]))?JSON.parse(obj).params[0]:JSON.parse(obj).params[1]);
        if (window.showModalDialog === undefined) {
            password = prompt("请输入您的登录密码","");
            if(password !== null && password !== "") {
                isLogin = SOF_Login(cName, password);
                if (!isLogin) {
                    resCount = SOF_GetPinRetryCount(cName);
                    alert("密码错误！剩余重试次数 "+ resCount + ' 次');
                }
            }
        }else {
          isLogin = windowShowModalDialog(cName);
        }
        if(isLogin) {
            getMessagePlugin(obj, callback);
        }else {
            callback(response.retValue);
        }
    }else{
        callback(decodeURIComponent(response.retValue));
    }
}

export function getMessage(obj) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        getMessagePlugin(obj, function (res) {
            console.log(res)
            retValue = res;
        });
        return retValue;
    }else {
        return getMessageTemp(obj).then(function(response) {
            if(response === "") {
                return new SOF_GetLastError().then(function(retCode){
                    if(retCode === 184550659) {
                        var cName = (isNaN(JSON.parse(obj).params[0]))?JSON.parse(obj).params[0]:JSON.parse(obj).params[1];
                        return showLoginPrompt(cName).then(function(res){
							if(res === "cancel") {
								return Promise.resolve(response);
							}
							else if (res === "false"){
							}else {
								return getMessage(obj);
							}
                        });
                    }else{
                        return Promise.resolve(response);
                    }
                });
            }else{
                 return Promise.resolve(response);
             }
        })
    }
}

/**
 * 获取接口版本信息 SOF_GetVersion
 *
 * @return  非空             成功
            空              失败
 */
export function SOF_GetVersion() {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_GetVersion","params":[]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 设置签名算法 SOF_SetSignMethod
 *
 * @param   SignMethod      [IN]            签名算法标识，参见GM/T0006-2012 5.2.4签名算法标识
 * @return  0               成功
            非0             失败
 */
export function SOF_SetSignMethod(SignMethod) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOF_SetSignMethod","params":[SignMethod]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 获得当前签名算法 SOF_GetSignMethod
 *
 * @return  非0             已设置的签名算法
            0               失败
 */
export function SOF_GetSignMethod() {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return 0;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(0);
        }
    }
    var objTemp = {"action":"SOF_GetSignMethod","params":[]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 设置加密算法 SOF_SetEncryptMethod
 *
 * @param   EncryptMethod   [IN]            对称算法标识，参见GM/T0006-2012 5.2.1分组密码算法标识
 * @return  0               成功
            非0             失败
 */
export function SOF_SetEncryptMethod(EncryptMethod) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action": "SOF_SetEncryptMethod", "params": [EncryptMethod]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 获得当前加密算法 SOF_GetEncryptMethod
 *
 * @return  非0             已设置的对称加解密算法
            0               失败
 */
function SOF_GetEncryptMethod() {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return 0;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(0);
        }
    }
    var objTemp = {"action":"SOF_GetEncryptMethod","params":[]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 获得设备信息 SOF_GetDeviceInfo
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   type            [IN]            信息类别，参见GM/T0006-2012 6.5.1设备信息标识
 * @return  非空            设备信息
            空              失败
 */
function SOF_GetDeviceInfo(ContainerName, type) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_GetDeviceInfo","params":[encodeURIFun(ContainerName), type]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]获得设备列表 SOFE_GetDeviceList
 *
 * @return  非空            设备列表
            空              失败
 */
function SOFE_GetDeviceList() {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_GetDeviceList","params":[]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 创建设备容器 SOFE_CreateContainer
 *
 * @param   DeviceName      [IN]            设备名称
 * @param   ContainerName   [IN]            可选参数，默认值为空。当参数不存在时，使用随机容器名创建容器；当参数存在时，则使用传入的容器名创建容器
 * @param   PassWd          [IN]            可选参数，默认值为"12345678"。当参数不存在时，使用默认值校验用户口令；当参数存在时，则使用传入的值校验用户口令
 * @return  非空            返回创建成功的容器名
            空              失败
 */
function SOFE_CreateContainer(DeviceName, ContainerName, PassWd) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var tempContainerName;
    var tempPassWd;
    if(typeof ContainerName === "undefined") {
        tempContainerName = ""
    }else {
        tempContainerName = encodeURIFun(ContainerName);
    }
    if(typeof PassWd === "undefined") {
        tempPassWd = ""
    }else {
        tempPassWd = PassWd;
    }
    var objTemp = {"action":"SOFE_CreateContainer","params":[encodeURIFun(DeviceName), tempContainerName||"", tempPassWd||""]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]设置证书过滤项 SOFE_SetCertFilter
 *
 * @param   CertIssuer      [IN]            证书颁发者。如果该参数为空，则不过滤证书颁发者
 * @param   CertSubject     [IN]            证书拥有者。如果该参数为空，则不过滤证书拥有者
 * @param   CertValid     	[IN]            证书有效性。1为过滤证书有效性，0为不过滤证书有效性
 * @return  0	            成功
            非0		        失败
 */
function SOFE_SetCertFilter(CertIssuer, CertSubject, CertValid) {
	if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
	var tempCertIssuer;
    var tempCertSubject;
    if(typeof CertIssuer === "undefined") {
        tempCertIssuer = ""
    }else {
        tempCertIssuer = encodeURIFun(CertIssuer);
    }
    if(typeof CertSubject === "undefined") {
        tempCertSubject = ""
    }else {
        tempCertSubject = encodeURIFun(CertSubject);
    }
    var objTemp = {"action":"SOFE_SetCertFilter","params":[tempCertIssuer||"", tempCertSubject||"", CertValid]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 获得证书列表 SOF_GetUserList
 *
 * @return  非空            用户列表
            空              失败
 */
export function SOF_GetUserList() {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_GetUserList","params":[]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 校验证书口令 SOF_Login
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   PassWd          [IN]            设备的用户认证口令
 * @return  TRUE            成功
            FALSE           失败
 */
export function SOF_Login(ContainerName, PassWd) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return false;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(false);
        }
    }
    var objTemp = {"action":"SOF_Login","params":[encodeURIFun(ContainerName), PassWd]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 获得用户认证口令剩余重试次数 SOF_GetPinRetryCount
 *
 * @param   ContainerName   [IN]            证书容器名
 * @return  long            剩余口令重试次数，当重试次数小于或等于0时表示证书介质口令已被锁死
 */
function SOF_GetPinRetryCount(ContainerName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOF_GetPinRetryCount","params":[encodeURIFun(ContainerName)]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 修改证书口令 SOF_ChangePassWd
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   OldPassWd       [IN]            旧的用户认证口令
 * @param   NewPassWd       [IN]            新的用户认证口令
 * @return  TRUE            成功
            FALSE           失败
 */
function SOF_ChangePassWd(ContainerName, OldPassWd, NewPassWd) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return false;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(false);
        }
    }
    var objTemp = {"action":"SOF_ChangePassWd","params":[encodeURIFun(ContainerName), OldPassWd, NewPassWd]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]解锁证书口令 SOFE_UnblockPassWd
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   AdminPassWd     [IN]            管理员口令
 * @param   NewPassWd       [IN]            新的用户认证口令
 * @return  TRUE            成功
            FALSE           失败
 */
function SOFE_UnblockPassWd(ContainerName, AdminPassWd, NewPassWd) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return false;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(false);
        }
    }
    var objTemp = {"action":"SOFE_UnblockPassWd","params":[encodeURIFun(ContainerName), AdminPassWd, NewPassWd]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]导入签名证书 SOFE_ImportUserCert
 *
 * @param   ContainerName           [IN]            证书容器名
 * @param   Base64EncodeCert        [IN]            Base64编码的签名证书字符串
 * @return  0                       成功
            非0                     失败
 */
function SOFE_ImportUserCert(ContainerName, Base64EncodeCert) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOFE_ImportUserCert","params":[encodeURIFun(ContainerName), Base64EncodeCert]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]导入加密证书 SOFE_ImportExChangeUserCert
 *
 * @param   ContainerName           [IN]            证书容器名
 * @param   Base64EncodeCert        [IN]            Base64编码的加密证书字符串
 * @return  0                       成功
            非0                     失败
 */
function SOFE_ImportExChangeUserCert(ContainerName, Base64EncodeCert) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOFE_ImportExChangeUserCert","params":[encodeURIFun(ContainerName), Base64EncodeCert]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 导出签名证书 SOF_ExportUserCert
 *
 * @param   ContainerName   [IN]            证书容器名
 * @return  非空            Base64编码的签名证书字符串
            空              失败
 */
export function SOF_ExportUserCert(ContainerName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_ExportUserCert","params":[encodeURIFun(ContainerName)]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 导出加密证书 SOF_ExportExChangeUserCert
 *
 * @param   ContainerName   [IN]            证书容器名
 * @return  非空            Base64编码的加密证书字符串
            空              失败
 */
function SOF_ExportExChangeUserCert(ContainerName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_ExportExChangeUserCert","params":[encodeURIFun(ContainerName)]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]导入加密密钥对 SOFE_ImportECCKeyPair
 *
 * @param   ContainerName           [IN]            证书容器名
 * @param   EnvelopedKeyPair        [IN]            受保护的加密密钥对
 * @return  0                       成功
            非0                     失败
 */
function SOFE_ImportECCKeyPair(ContainerName, EnvelopedKeyPair) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOFE_ImportECCKeyPair","params":[encodeURIFun(ContainerName), EnvelopedKeyPair]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]产生签名证书请求 SOFE_GenerateP10
 *
 * @param   ContainerName           [IN]            证书容器名
 * @param   CertCSRInfo             [IN]            证书请求主题项字符串，字段定义符合X509标准，如“CN=张三,OU=产品研发部,O=SCCA,L=成都市,S=四川省,C=CN”
 * @param   NewContainerName        [IN]            可选参数，默认值为空。当该值为空时，从ContainerName产生签名证书请求；当该值不为空时，则创建新容器和签名密钥对，并产生签名证书请求
 * @return  非空                    Base64编码的签名证书请求
            空                      失败
 */
function SOFE_GenerateP10(ContainerName, CertCSRInfo, NewContainerName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var tempNewContainerName;
    if(typeof NewContainerName === "undefined") {
        tempNewContainerName = ""
    }else {
        tempNewContainerName = encodeURIFun(NewContainerName);
    }
    var objTemp = {"action":"SOFE_GenerateP10","params":[encodeURIFun(ContainerName), encodeURIFun(CertCSRInfo), tempNewContainerName||""]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]删除证书 SOFE_DeleteCert
 *
 * @param   ContainerName   [IN]            证书容器名
 * @return  0               成功
            非0             失败
 */
function SOFE_DeleteCert(ContainerName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOFE_DeleteCert","params":[encodeURIFun(ContainerName)]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 获得证书信息 SOF_GetCertInfo
 *
 * @param   Base64EncodeCert        [IN]            Base64编码的证书
 * @param   type                    [IN]            获取信息的类型，参加GM/T0006-2012 5.3.4证书解析项标识
 * @return  非空                    证书内指定类型的信息
            空                      失败
 */
export function SOF_GetCertInfo(Base64EncodeCert, type) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_GetCertInfo","params":[Base64EncodeCert, type]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 获得证书扩展信息 SOF_GetCertInfoByOid
 *
 * @param   Base64EncodeCert        [IN]            Base64编码的证书
 * @param   Oid                     [IN]            证书扩展对象ID，如“1.2.156.xxx”
 * @return  非空                    证书内私有扩展项OID对应的信息
            空                      失败
 */
function SOF_GetCertInfoByOid(Base64EncodeCert, Oid) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_GetCertInfoByOid","params":[Base64EncodeCert, Oid]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 验证证书有效性 SOF_ValidateCert
 *
 * @param   Base64EncodeCert        [IN]            Base64编码的证书
 * @return   0                      验证成功
             非0                    失败
                                     -1 证书不被信任
                                     -2 超过有效期范围
                                     -3 证书已作废
                                     -4 证书已冻结
                                     -5 证书未生效
                                     -6 其他错误
 */
function SOF_ValidateCert(Base64EncodeCert) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOF_ValidateCert","params":[Base64EncodeCert]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]获得文件列表 SOFE_EnumFiles
 *
 * @param   ContainerName   [IN]            证书容器名
 * @return  非空            文件列表
            空              失败
 */
function SOFE_EnumFiles(ContainerName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_EnumFiles","params":[encodeURIFun(ContainerName)]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]创建文件 SOFE_CreateFile
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   FileName        [IN]            文件名称，长度不能超过32字节
 * @param   FileSize        [IN]            文件大小
 * @param   ReadRights      [IN]            文件读权限，参见GM/T 0016-2012 6.4.12权限类型
 * @param   WriteRights     [IN]            文件写权限，参见GM/T 0016-2012 6.4.12权限类型
 * @return  0               成功
            非0             失败
 */
function SOFE_CreateFile(ContainerName, FileName, FileSize, ReadRights, WriteRights) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOFE_CreateFile","params":[encodeURIFun(ContainerName), encodeURIFun(FileName), FileSize, ReadRights, WriteRights]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]删除文件 SOFE_DeleteFile
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   FileName        [IN]            文件名称
 * @return  0               成功
            非0             失败
 */
function SOFE_DeleteFile(ContainerName, FileName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOFE_DeleteFile","params":[encodeURIFun(ContainerName), encodeURIFun(FileName)]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]读取文件 SOFE_ReadFile
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   FileName        [IN]            文件名称
 * @return  非空            Base64后的文件数据
            空              失败
 */
function SOFE_ReadFile(ContainerName, FileName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_ReadFile","params":[encodeURIFun(ContainerName), encodeURIFun(FileName)]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]写入文件 SOFE_WriteFile
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   FileName        [IN]            文件名称
 * @param   Data            [IN]            Base64编码的待写入数据
 * @return  0               成功
            非0             失败
 */
function SOFE_WriteFile(ContainerName, FileName, Data) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }
    var objTemp = {"action":"SOFE_WriteFile","params":[encodeURIFun(ContainerName), encodeURIFun(FileName), Data]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 数字签名 SOF_SignData
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   InData          [IN]            Base64编码的签名原文
 * @return  非空            Base64编码的PKCS#1格式签名值
            空              失败
 */
export function SOF_SignData(ContainerName, InData) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_SignData","params":[encodeURIFun(ContainerName), InData]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 验证签名 SOF_VerifySignedData
 *
 * @param   Base64EncodeCert        [IN]            Base64编码的证书
 * @param   InData                  [IN]            Base64编码的签名原文
 * @param   SignValue               [IN]            Base64编码的PKCS#1格式签名值
 * @return  TRUE            		成功
            FALSE           		失败
 */
function SOF_VerifySignedData(Base64EncodeCert, InData, SignValue) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return false;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(false);
        }
    }
    var objTemp = {"action":"SOF_VerifySignedData","params":[Base64EncodeCert, InData, SignValue]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 文件签名 SOF_SignFile
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   InFile          [IN]            原文文件路径，包含文件名
 * @return  非空            Base64编码的PKCS#1格式签名值
            空              失败
 */
function SOF_SignFile(ContainerName, InFile) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_SignFile","params":[encodeURIFun(ContainerName), InFile]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 文件验签 SOF_VerifySignedFile
 *
 * @param   Base64EncodeCert        [IN]            Base64编码的证书
 * @param   InFile                  [IN]            原文文件路径，包含文件名
 * @param   SignValue               [IN]            Base64编码的PKCS#1格式签名值
 * @return  TRUE                    成功
            FALSE                   失败
 */
function SOF_VerifySignedFile(Base64EncodeCert, InFile, SignValue) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return false;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(false);
        }
    }
    var objTemp = {"action":"SOF_VerifySignedFile","params":[Base64EncodeCert, InFile, SignValue]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 加密数据 SOF_EncryptData
 *
 * @param   Base64EncodeCert        [IN]            Base64编码的外部加密证书
 * @param   InData                  [IN]            Base64编码的待加密明文
 * @return  非空                    Base64编码的PKCS#7格式密文数据
            空                      失败
 */
function SOF_EncryptData(Base64EncodeCert, InData) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_EncryptData","params":[Base64EncodeCert, InData]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 解密数据 SOF_DecryptData
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   InData          [IN]            待解密的Base64编码的PKCS#7格式密文数据
 * @return  非空            Base64编码的解密后明文数据
            空              失败
 */
function SOF_DecryptData(ContainerName, InData) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_DecryptData","params":[encodeURIFun(ContainerName), InData]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 文件加密 SOF_EncryptFile
 *
 * @param   Base64EncodeCert        [IN]            Base64编码的外部加密证书
 * @param   InFile                  [IN]            待加密的文件路径，包含文件名
 * @param   OutFile                 [IN]            加密后的文件输出路径，包含文件名
 * @return  TRUE                    成功
            FALSE                   失败
 */
function SOF_EncryptFile(Base64EncodeCert, InFile, OutFile) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return false;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(false);
        }
    }
    var objTemp = {"action":"SOF_EncryptFile","params":[Base64EncodeCert, InFile, OutFile]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 文件解密 SOF_DecryptFile
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   InFile          [IN]            待解密的文件路径，包含文件名
 * @param   OutFile         [IN]            解密后的文件输出路径，包含文件名
 * @return  TRUE            成功
            FALSE           失败
 */
function SOF_DecryptFile(ContainerName, InFile, OutFile) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return false;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(false);
        }
    }
    var objTemp = {"action":"SOF_DecryptFile","params":[encodeURIFun(ContainerName), InFile, OutFile]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 消息签名 SOF_SignMessage
 *
 * @param   flag            [IN]            是否带原文的标识，1为不带原文，0为带原文
 * @param   ContainerName   [IN]            证书容器名
 * @param   InData          [IN]            Base64编码的签名原文
 * @return  非空            Base64编码的PKCS#7格式签名值
            空              失败
 */
export function SOF_SignMessage(flag, ContainerName, InData) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_SignMessage","params":[flag, encodeURIFun(ContainerName), InData]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]国际算法PKCS7格式消息签名 SOFE_SignPKCS7Message
 *
 * @param   flag            [IN]            是否带原文的标识，1为不带原文，0为带原文
 * @param   ContainerName   [IN]            证书容器名
 * @param   InData          [IN]            Base64编码的签名原文
 * @return  非空            Base64编码的国际算法PKCS#7格式签名值
            空              失败
 */
function SOFE_SignPKCS7Message(flag, ContainerName, InData) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_SignPKCS7Message","params":[flag, encodeURIFun(ContainerName), InData]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 验证消息签名 SOF_VerifySignedMessage
 *
 * @param   SignedMessage   [IN]            Base64编码的PKCS#7格式签名值
 * @param   InData          [IN]            Base64编码的签名原文，如果签名结果带原文，则本参数可为空
 * @return  TRUE            成功
            FALSE           失败
 */
function SOF_VerifySignedMessage(SignedMessage, InData){
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return false;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(false);
        }
    }
    var tempInData;
    if(typeof InData === "undefined") {
        tempInData = ""
    }else {
        tempInData = InData;
    }
    var objTemp = {"action":"SOF_VerifySignedMessage","params":[SignedMessage, tempInData||""]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 解析消息签名 SOF_GetInfoFromSignedMessage
 *
 * @param   SignedMessage   [IN]            Base64编码的PKCS#7格式签名值
 * @param   type            [IN]            信息类型，1表示解析原文，2表示解析Base64编码的签名者证书，3表示解析Base64编码的签名值
 * @return  非空            解析出的信息
            空              失败
 */
function SOF_GetInfoFromSignedMessage(SignedMessage, type) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_GetInfoFromSignedMessage","params":[SignedMessage, type]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]数据签名格式转消息签名格式 SOFE_GenerateSignMessage
 *
 * @param   Base64EncodeCert        [IN]            Base64编码的签名者证书
 * @param   SignValue               [IN]            Base64编码的PKCS#1格式的数据签名结果
 * @param   InData                  [IN]            Base64编码的签名原文，如无需带原文，该参数可为空
 * @return  非空                    转换后的Base64编码的消息签名PKCS#7格式数据
            空                      失败
 */
function SOFE_GenerateSignMessage(Base64EncodeCert, SignValue, InData) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_GenerateSignMessage","params":[Base64EncodeCert, SignValue, InData||""]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 产生随机数 SOF_GenRandom
 *
 * @param   RandomLen       [IN]            待产生的随机数长度
 * @return  非空            Base64编码的随机数值
            空              失败
 */
function SOF_GenRandom(RandomLen) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOF_GenRandom","params":[RandomLen]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]密码杂凑运算 SOFE_Digest
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   InData          [IN]            Base64编码的待计算数据
 * @return  非空            Base64编码的密码杂凑运算结果
            空              失败
 */
function SOFE_Digest(ContainerName, InData) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_Digest","params":[encodeURIFun(ContainerName), InData]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]导出会话密钥 SOFE_ECCExportSessionKey
 *
 * @param   ContainerName   [IN]            证书容器名
 * @return  非空            Base64编码的会话密钥密文
            空              失败
 */
function SOFE_ECCExportSessionKey(ContainerName) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_ECCExportSessionKey","params":[encodeURIFun(ContainerName)]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]消息鉴别码运算 SOFE_Mac
 *
 * @param   ContainerName   [IN]            证书容器名
 * @param   SessionKey      [IN]            Base64编码的会话密钥密文
 * @param   InData          [IN]            Base64编码的待计算的数据
 * @param   IV              [IN]            Base64编码的初始向量，最大长度为32字节
 * @return  非空            Base64编码的消息鉴别码运算结果
            空              失败
 */
function SOFE_Mac(ContainerName, SessionKey, InData, IV) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_Mac","params":[encodeURIFun(ContainerName), SessionKey, InData, IV]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]会话密钥转保护 SOFE_WrapSessionKey
 *
 * @param   ContainerName           [IN]            证书容器名
 * @param   SessionKey              [IN]            Base64编码的会话密钥密文
 * @param   Base64EncodeCert        [IN]            Base64编码的外部加密证书
 * @return  非空                    Base64编码的转保护后的会话密钥
            空                      失败
 */
function SOFE_WrapSessionKey(ContainerName, SessionKey, Base64EncodeCert) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_WrapSessionKey","params":[encodeURIFun(ContainerName), SessionKey, Base64EncodeCert]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]消息认证码运算 SOFE_HMAC
 *
 * @param   ContainerName           [IN]            证书容器名
 * @param   Base64EncodeCert        [IN]            Base64编码的外部加密证书
 * @param   InData                  [IN]            Base64编码的待计算数据
 * @return  非空                    Base64编码的消息认证码运算结果
            空                      失败
 */
function SOFE_HMAC(ContainerName, Base64EncodeCert, InData) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return nullStr;
        }
    }else {
        if(!isConnect){
            return Promise.resolve(nullStr);
        }
    }
    var objTemp = {"action":"SOFE_HMAC","params":[encodeURIFun(ContainerName), Base64EncodeCert, InData]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * 获取最新的错误代码 SOF_GetLastError
 *
 * @return  long            错误代码，参见错误代码表
 */
function SOF_GetLastError() {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return SOR_TimeoutErr;
        }
    }else if(browserType === BROWSER_TYPE_WSS){
        if(!isConnect){
            return Promise.resolve(SOR_TimeoutErr);
        }
    }else if(browserType === BROWSER_TYPE_HTTPS){
		return Promise.resolve(SOR_NotSupportYetErr);
	}
    var objTemp = {"action":"SOF_GetLastError","params":[]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

/**
 * [扩展接口]获取错误描述 SOF_GetErrorMessage
 *
 * @param   ErrorCode      [IN]            错误代码
 * @return  非空           返回的错误代码描述
            空             失败
 */
function SOFE_GetErrorMessage(ErrorCode) {
    if(browserType === BROWSER_TYPE_NPAPI) {
        if(plugin() === null || typeof plugin().valid === "undefined") {
            return '服务超时';
        }
    }else if(browserType === BROWSER_TYPE_WSS) {
        if(!isConnect){
            return Promise.resolve('服务超时');
        }
    }else if(browserType === BROWSER_TYPE_HTTPS){
		return Promise.resolve('不支持的服务');
	}
    var objTemp = {"action":"SOFE_GetErrorMessage","params":[ErrorCode]};
    var obj = JSON.stringify(objTemp);
    return getMessage(obj);
}

//时间格式转换
function toLocalTime(time) {
    if (time === "null")
        return "null";
    return time.substr(0,4) + '-' + time.substr(4,2) + '-' + time.substr(6,2) + ' ' + time.substr(8,2) + ':' + time.substr(10,2) + ':' + time.substr(12,2);
}

//base64编码
export function base64Encode(input) {
    var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    var output = "", chr1, chr2, chr3, enc1, enc2, enc3, enc4, i = 0;
    input = _utf8_encode(input);
    while (i < input.length) {
        chr1 = input.charCodeAt(i++);
        chr2 = input.charCodeAt(i++);
        chr3 = input.charCodeAt(i++);
        enc1 = chr1 >> 2;
        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
        enc4 = chr3 & 63;
        if (isNaN(chr2)) {
            enc3 = enc4 = 64;
        } else if (isNaN(chr3)) {
            enc4 = 64;
        }
        output = output +
            _keyStr.charAt(enc1) + _keyStr.charAt(enc2) +
            _keyStr.charAt(enc3) + _keyStr.charAt(enc4);
    }
    return output;
}

//base64解码
export function base64Decode(input) {
    var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    var output = "", chr1, chr2, chr3, enc1, enc2, enc3, enc4, i = 0;
    input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
    while (i < input.length) {
        enc1 = _keyStr.indexOf(input.charAt(i++));
        enc2 = _keyStr.indexOf(input.charAt(i++));
        enc3 = _keyStr.indexOf(input.charAt(i++));
        enc4 = _keyStr.indexOf(input.charAt(i++));
        chr1 = (enc1 << 2) | (enc2 >> 4);
        chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
        chr3 = ((enc3 & 3) << 6) | enc4;
        output = output + String.fromCharCode(chr1);
        if (enc3 != 64) {
            output = output + String.fromCharCode(chr2);
        }
        if (enc4 != 64) {
            output = output + String.fromCharCode(chr3);
        }
    }
    output = _utf8_decode(output);
    return output;
}

//utf8编码
function _utf8_encode(string) {
    string = string.replace(/\r\n/g,"\n");
    var utftext = "";
    for (var n = 0; n < string.length; n++) {
        var c = string.charCodeAt(n);
        if (c < 128) {
            utftext += String.fromCharCode(c);
        } else if((c > 127) && (c < 2048)) {
            utftext += String.fromCharCode((c >> 6) | 192);
            utftext += String.fromCharCode((c & 63) | 128);
        } else {
            utftext += String.fromCharCode((c >> 12) | 224);
            utftext += String.fromCharCode(((c >> 6) & 63) | 128);
            utftext += String.fromCharCode((c & 63) | 128);
        }

    }
    return utftext;
}

//utf8解码
export function _utf8_decode(utftext) {
    var string = "", i = 0, c = 0, c1 = 0, c2 = 0, c3 = 0;
    while ( i < utftext.length ) {
        c = utftext.charCodeAt(i);
        if (c < 128) {
            string += String.fromCharCode(c);
            i++;
        } else if((c > 191) && (c < 224)) {
            c2 = utftext.charCodeAt(i+1);
            string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
            i += 2;
        } else {
            c2 = utftext.charCodeAt(i+1);
            c3 = utftext.charCodeAt(i+2);
            string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
            i += 3;
        }
    }
    return string;
}

function atob_ex(input) {
    var _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    var str = (String (input)).replace (/[=]+$/, '');
    if (str.length % 4 === 1) {
        console.error("'atob' failed: The string to be decoded is not correctly encoded.");
    }
    for (
        var bc = 0, bs, buffer, idx = 0, output = '';
        buffer = str.charAt (idx++);
        ~buffer && (bs = bc % 4 ? bs * 64 + buffer : buffer,
        bc++ % 4) ? output += String.fromCharCode (255 & bs >> (-2 * bc & 6)) : 0
    ) {
        buffer = _keyStr.indexOf (buffer);
    }
    return output;
}


//base64转16进制字符串
export function base64toHEX(base64) {
    if (base64 === "null" || base64 === "")
        return "null";
    var raw = atob_ex(base64);
    var HEX = '';
    for(var i =0;i<raw.length;i++) {
        var _hex = raw.charCodeAt(i).toString(16);
        HEX +=(_hex.length ===2 ? _hex : '0' + _hex);
    }
    return HEX.toUpperCase();
}

//URI编码
function encodeURIFun(data)
{
    if(browserType === BROWSER_TYPE_NPAPI) {
        return encodeURI(data);
    }else {
        return data;
    }
}

(function() { if (window.WEB_SOCKET_FORCE_FLASH) { } else if (window.WebSocket) { return; } else if (window.MozWebSocket) { window.WebSocket = MozWebSocket; return; } var logger; if (window.WEB_SOCKET_LOGGER) { logger = WEB_SOCKET_LOGGER; } else if (window.console && window.console.log && window.console.error) { logger = window.console; } else { logger = {log: function(){ }, error: function(){ }}; } if (swfobject.getFlashPlayerVersion().major < 10) { logger.error("Flash Player >= 10.0.0 is required."); return; } if (location.protocol == "file:") { logger.error( "WARNING: web-socket-js doesn't work in file:///... URL " + "unless you set Flash Security Settings properly. " + "Open the page via Web server i.e. http://..."); } window.WebSocket = function(url, protocols, proxyHost, proxyPort, headers) { var self = this; self.__id = WebSocket.__nextId++; WebSocket.__instances[self.__id] = self; self.readyState = WebSocket.CONNECTING; self.bufferedAmount = 0; self.__events = {}; if (!protocols) { protocols = []; } else if (typeof protocols == "string") { protocols = [protocols]; } self.__createTask = setTimeout(function() { WebSocket.__addTask(function() { self.__createTask = null; WebSocket.__flash.create( self.__id, url, protocols, proxyHost || null, proxyPort || 0, headers || null); }); }, 0); }; WebSocket.prototype.send = function(data) { if (this.readyState == WebSocket.CONNECTING) { throw "INVALID_STATE_ERR: Web Socket connection has not been established"; } var result = WebSocket.__flash.send(this.__id, encodeURIComponent(data)); if (result < 0) { return true; } else { this.bufferedAmount += result; return false; } }; WebSocket.prototype.close = function() { if (this.__createTask) { clearTimeout(this.__createTask); this.__createTask = null; this.readyState = WebSocket.CLOSED; return; } if (this.readyState == WebSocket.CLOSED || this.readyState == WebSocket.CLOSING) { return; } this.readyState = WebSocket.CLOSING; WebSocket.__flash.close(this.__id); }; WebSocket.prototype.addEventListener = function(type, listener, useCapture) { if (!(type in this.__events)) { this.__events[type] = []; } this.__events[type].push(listener); }; WebSocket.prototype.removeEventListener = function(type, listener, useCapture) { if (!(type in this.__events)) return; var events = this.__events[type]; for (var i = events.length - 1; i >= 0; --i) { if (events[i] === listener) { events.splice(i, 1); break; } } }; WebSocket.prototype.dispatchEvent = function(event) { var events = this.__events[event.type] || []; for (var i = 0; i < events.length; ++i) { events[i](event); } var handler = this["on" + event.type]; if (handler) handler.apply(this, [event]); }; WebSocket.prototype.__handleEvent = function(flashEvent) { if ("readyState" in flashEvent) { this.readyState = flashEvent.readyState; } if ("protocol" in flashEvent) { this.protocol = flashEvent.protocol; } var jsEvent; if (flashEvent.type == "open" || flashEvent.type == "error") { jsEvent = this.__createSimpleEvent(flashEvent.type); } else if (flashEvent.type == "close") { jsEvent = this.__createSimpleEvent("close"); jsEvent.wasClean = flashEvent.wasClean ? true : false; jsEvent.code = flashEvent.code; jsEvent.reason = flashEvent.reason; } else if (flashEvent.type == "message") { var data = decodeURIComponent(flashEvent.message); jsEvent = this.__createMessageEvent("message", data); } else { throw "unknown event type: " + flashEvent.type; } this.dispatchEvent(jsEvent); }; WebSocket.prototype.__createSimpleEvent = function(type) { if (document.createEvent && window.Event) { var event = document.createEvent("Event"); event.initEvent(type, false, false); return event; } else { return {type: type, bubbles: false, cancelable: false}; } }; WebSocket.prototype.__createMessageEvent = function(type, data) { if (window.MessageEvent && typeof(MessageEvent) == "function" && !window.opera) { return new MessageEvent("message", { "view": window, "bubbles": false, "cancelable": false, "data": data }); } else if (document.createEvent && window.MessageEvent && !window.opera) { var event = document.createEvent("MessageEvent");  event.initMessageEvent("message", false, false, data, null, null, window, null); return event; } else { return {type: type, data: data, bubbles: false, cancelable: false}; } }; WebSocket.CONNECTING = 0; WebSocket.OPEN = 1; WebSocket.CLOSING = 2; WebSocket.CLOSED = 3; WebSocket.__isFlashImplementation = true; WebSocket.__initialized = false; WebSocket.__flash = null; WebSocket.__instances = {}; WebSocket.__tasks = []; WebSocket.__nextId = 0; WebSocket.loadFlashPolicyFile = function(url){ WebSocket.__addTask(function() { WebSocket.__flash.loadManualPolicyFile(url); }); }; WebSocket.__initialize = function() { if (WebSocket.__initialized) return; WebSocket.__initialized = true; if (WebSocket.__swfLocation) { window.WEB_SOCKET_SWF_LOCATION = WebSocket.__swfLocation; } if (!window.WEB_SOCKET_SWF_LOCATION) { logger.error("[WebSocket] set WEB_SOCKET_SWF_LOCATION to location of wss.swf"); return; } if (!window.WEB_SOCKET_SUPPRESS_CROSS_DOMAIN_SWF_ERROR && !WEB_SOCKET_SWF_LOCATION.match(/(^|\/)WebSocketMainInsecure\.swf(\?.*)?$/) && WEB_SOCKET_SWF_LOCATION.match(/^\w+:\/\/([^\/]+)/)) { var swfHost = RegExp.$1; if (location.host != swfHost) { logger.error( "[WebSocket] You must host HTML and wss.swf in the same host " + "('" + location.host + "' != '" + swfHost + "'). " + "See also 'How to host HTML file and SWF file in different domains' section " + "in README.md. If you use WebSocketMainInsecure.swf, you can suppress this message " + "by WEB_SOCKET_SUPPRESS_CROSS_DOMAIN_SWF_ERROR = true;"); } } var container = document.createElement("div"); container.id = "webSocketContainer"; container.style.position = "absolute"; if (WebSocket.__isFlashLite()) { container.style.left = "0px"; container.style.top = "0px"; } else { container.style.left = "-100px"; container.style.top = "-100px"; } var holder = document.createElement("div"); holder.id = "webSocketFlash"; container.appendChild(holder); document.body.appendChild(container); swfobject.embedSWF( WEB_SOCKET_SWF_LOCATION, "webSocketFlash", "1", "1" , "10.0.0", null, null, {hasPriority: true, swliveconnect : true, allowScriptAccess: "always"}, null, function(e) { if (!e.success) { logger.error("[WebSocket] swfobject.embedSWF failed"); } } ); }; WebSocket.__onFlashInitialized = function() { setTimeout(function() { WebSocket.__flash = document.getElementById("webSocketFlash"); WebSocket.__flash.setCallerUrl(location.href); WebSocket.__flash.setDebug(!!window.WEB_SOCKET_DEBUG); for (var i = 0; i < WebSocket.__tasks.length; ++i) { WebSocket.__tasks[i](); } WebSocket.__tasks = []; }, 0); }; WebSocket.__onFlashEvent = function() { setTimeout(function() { try { var events = WebSocket.__flash.receiveEvents(); for (var i = 0; i < events.length; ++i) { WebSocket.__instances[events[i].webSocketId].__handleEvent(events[i]); } } catch (e) { logger.error(e); } }, 0); return true; }; WebSocket.__log = function(message) { logger.log(decodeURIComponent(message)); }; WebSocket.__error = function(message) { logger.error(decodeURIComponent(message)); }; WebSocket.__addTask = function(task) { if (WebSocket.__flash) { task(); } else { WebSocket.__tasks.push(task); } }; WebSocket.__isFlashLite = function() { if (!window.navigator || !window.navigator.mimeTypes) { return false; } var mimeType = window.navigator.mimeTypes["application/x-shockwave-flash"]; if (!mimeType || !mimeType.enabledPlugin || !mimeType.enabledPlugin.filename) { return false; } return mimeType.enabledPlugin.filename.match(/flashlite/i) ? true : false; }; if (!window.WEB_SOCKET_DISABLE_AUTO_INITIALIZATION) { swfobject.addDomLoadEvent(function() { WebSocket.__initialize(); }); } })();
var swfobject=function(){var D="undefined",r="object",S="Shockwave Flash",W="ShockwaveFlash.ShockwaveFlash",q="application/x-shockwave-flash",R="SWFObjectExprInst",x="onreadystatechange",O=window,j=document,t=navigator,T=false,U=[h],o=[],N=[],I=[],l,Q,E,B,J=false,a=false,n,G,m=true,M=function(){var aa=typeof j.getElementById!=D&&typeof j.getElementsByTagName!=D&&typeof j.createElement!=D,ah=t.userAgent.toLowerCase(),Y=t.platform.toLowerCase(),ae=Y?/win/.test(Y):/win/.test(ah),ac=Y?/mac/.test(Y):/mac/.test(ah),af=/webkit/.test(ah)?parseFloat(ah.replace(/^.*webkit\/(\d+(\.\d+)?).*$/,"$1")):false,X=!+"\v1",ag=[0,0,0],ab=null;if(typeof t.plugins!=D&&typeof t.plugins[S]==r){ab=t.plugins[S].description;if(ab&&!(typeof t.mimeTypes!=D&&t.mimeTypes[q]&&!t.mimeTypes[q].enabledPlugin)){T=true;X=false;ab=ab.replace(/^.*\s+(\S+\s+\S+$)/,"$1");ag[0]=parseInt(ab.replace(/^(.*)\..*$/,"$1"),10);ag[1]=parseInt(ab.replace(/^.*\.(.*)\s.*$/,"$1"),10);ag[2]=/[a-zA-Z]/.test(ab)?parseInt(ab.replace(/^.*[a-zA-Z]+(.*)$/,"$1"),10):0}}else{if(typeof O.ActiveXObject!=D){try{var ad=new ActiveXObject(W);if(ad){ab=ad.GetVariable("$version");if(ab){X=true;ab=ab.split(" ")[1].split(",");ag=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}}catch(Z){}}}return{w3:aa,pv:ag,wk:af,ie:X,win:ae,mac:ac}}(),k=function(){if(!M.w3){return}if((typeof j.readyState!=D&&j.readyState=="complete")||(typeof j.readyState==D&&(j.getElementsByTagName("body")[0]||j.body))){f()}if(!J){if(typeof j.addEventListener!=D){j.addEventListener("DOMContentLoaded",f,false)}if(M.ie&&M.win){j.attachEvent(x,function(){if(j.readyState=="complete"){j.detachEvent(x,arguments.callee);f()}});if(O==top){(function(){if(J){return}try{j.documentElement.doScroll("left")}catch(X){setTimeout(arguments.callee,0);return}f()})()}}if(M.wk){(function(){if(J){return}if(!/loaded|complete/.test(j.readyState)){setTimeout(arguments.callee,0);return}f()})()}s(f)}}();function f(){if(J){return}try{var Z=j.getElementsByTagName("body")[0].appendChild(C("span"));Z.parentNode.removeChild(Z)}catch(aa){return}J=true;var X=U.length;for(var Y=0;Y<X;Y++){U[Y]()}}function K(X){if(J){X()}else{U[U.length]=X}}function s(Y){if(typeof O.addEventListener!=D){O.addEventListener("load",Y,false)}else{if(typeof j.addEventListener!=D){j.addEventListener("load",Y,false)}else{if(typeof O.attachEvent!=D){i(O,"onload",Y)}else{if(typeof O.onload=="function"){var X=O.onload;O.onload=function(){X();Y()}}else{O.onload=Y}}}}}function h(){if(T){V()}else{H()}}function V(){var X=j.getElementsByTagName("body")[0];var aa=C(r);aa.setAttribute("type",q);var Z=X.appendChild(aa);if(Z){var Y=0;(function(){if(typeof Z.GetVariable!=D){var ab=Z.GetVariable("$version");if(ab){ab=ab.split(" ")[1].split(",");M.pv=[parseInt(ab[0],10),parseInt(ab[1],10),parseInt(ab[2],10)]}}else{if(Y<10){Y++;setTimeout(arguments.callee,10);return}}X.removeChild(aa);Z=null;H()})()}else{H()}}function H(){var ag=o.length;if(ag>0){for(var af=0;af<ag;af++){var Y=o[af].id;var ab=o[af].callbackFn;var aa={success:false,id:Y};if(M.pv[0]>0){var ae=c(Y);if(ae){if(F(o[af].swfVersion)&&!(M.wk&&M.wk<312)){w(Y,true);if(ab){aa.success=true;aa.ref=z(Y);ab(aa)}}else{if(o[af].expressInstall&&A()){var ai={};ai.data=o[af].expressInstall;ai.width=ae.getAttribute("width")||"0";ai.height=ae.getAttribute("height")||"0";if(ae.getAttribute("class")){ai.styleclass=ae.getAttribute("class")}if(ae.getAttribute("align")){ai.align=ae.getAttribute("align")}var ah={};var X=ae.getElementsByTagName("param");var ac=X.length;for(var ad=0;ad<ac;ad++){if(X[ad].getAttribute("name").toLowerCase()!="movie"){ah[X[ad].getAttribute("name")]=X[ad].getAttribute("value")}}P(ai,ah,Y,ab)}else{p(ae);if(ab){ab(aa)}}}}}else{w(Y,true);if(ab){var Z=z(Y);if(Z&&typeof Z.SetVariable!=D){aa.success=true;aa.ref=Z}ab(aa)}}}}}function z(aa){var X=null;var Y=c(aa);if(Y&&Y.nodeName=="OBJECT"){if(typeof Y.SetVariable!=D){X=Y}else{var Z=Y.getElementsByTagName(r)[0];if(Z){X=Z}}}return X}function A(){return !a&&F("6.0.65")&&(M.win||M.mac)&&!(M.wk&&M.wk<312)}function P(aa,ab,X,Z){a=true;E=Z||null;B={success:false,id:X};var ae=c(X);if(ae){if(ae.nodeName=="OBJECT"){l=g(ae);Q=null}else{l=ae;Q=X}aa.id=R;if(typeof aa.width==D||(!/%$/.test(aa.width)&&parseInt(aa.width,10)<310)){aa.width="310"}if(typeof aa.height==D||(!/%$/.test(aa.height)&&parseInt(aa.height,10)<137)){aa.height="137"}j.title=j.title.slice(0,47)+" - Flash Player Installation";var ad=M.ie&&M.win?"ActiveX":"PlugIn",ac="MMredirectURL="+O.location.toString().replace(/&/g,"%26")+"&MMplayerType="+ad+"&MMdoctitle="+j.title;if(typeof ab.flashvars!=D){ab.flashvars+="&"+ac}else{ab.flashvars=ac}if(M.ie&&M.win&&ae.readyState!=4){var Y=C("div");X+="SWFObjectNew";Y.setAttribute("id",X);ae.parentNode.insertBefore(Y,ae);ae.style.display="none";(function(){if(ae.readyState==4){ae.parentNode.removeChild(ae)}else{setTimeout(arguments.callee,10)}})()}u(aa,ab,X)}}function p(Y){if(M.ie&&M.win&&Y.readyState!=4){var X=C("div");Y.parentNode.insertBefore(X,Y);X.parentNode.replaceChild(g(Y),X);Y.style.display="none";(function(){if(Y.readyState==4){Y.parentNode.removeChild(Y)}else{setTimeout(arguments.callee,10)}})()}else{Y.parentNode.replaceChild(g(Y),Y)}}function g(ab){var aa=C("div");if(M.win&&M.ie){aa.innerHTML=ab.innerHTML}else{var Y=ab.getElementsByTagName(r)[0];if(Y){var ad=Y.childNodes;if(ad){var X=ad.length;for(var Z=0;Z<X;Z++){if(!(ad[Z].nodeType==1&&ad[Z].nodeName=="PARAM")&&!(ad[Z].nodeType==8)){aa.appendChild(ad[Z].cloneNode(true))}}}}}return aa}function u(ai,ag,Y){var X,aa=c(Y);if(M.wk&&M.wk<312){return X}if(aa){if(typeof ai.id==D){ai.id=Y}if(M.ie&&M.win){var ah="";for(var ae in ai){if(ai[ae]!=Object.prototype[ae]){if(ae.toLowerCase()=="data"){ag.movie=ai[ae]}else{if(ae.toLowerCase()=="styleclass"){ah+=' class="'+ai[ae]+'"'}else{if(ae.toLowerCase()!="classid"){ah+=" "+ae+'="'+ai[ae]+'"'}}}}}var af="";for(var ad in ag){if(ag[ad]!=Object.prototype[ad]){af+='<param name="'+ad+'" value="'+ag[ad]+'" />'}}aa.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"'+ah+">"+af+"</object>";N[N.length]=ai.id;X=c(ai.id)}else{var Z=C(r);Z.setAttribute("type",q);for(var ac in ai){if(ai[ac]!=Object.prototype[ac]){if(ac.toLowerCase()=="styleclass"){Z.setAttribute("class",ai[ac])}else{if(ac.toLowerCase()!="classid"){Z.setAttribute(ac,ai[ac])}}}}for(var ab in ag){if(ag[ab]!=Object.prototype[ab]&&ab.toLowerCase()!="movie"){e(Z,ab,ag[ab])}}aa.parentNode.replaceChild(Z,aa);X=Z}}return X}function e(Z,X,Y){var aa=C("param");aa.setAttribute("name",X);aa.setAttribute("value",Y);Z.appendChild(aa)}function y(Y){var X=c(Y);if(X&&X.nodeName=="OBJECT"){if(M.ie&&M.win){X.style.display="none";(function(){if(X.readyState==4){b(Y)}else{setTimeout(arguments.callee,10)}})()}else{X.parentNode.removeChild(X)}}}function b(Z){var Y=c(Z);if(Y){for(var X in Y){if(typeof Y[X]=="function"){Y[X]=null}}Y.parentNode.removeChild(Y)}}function c(Z){var X=null;try{X=j.getElementById(Z)}catch(Y){}return X}function C(X){return j.createElement(X)}function i(Z,X,Y){Z.attachEvent(X,Y);I[I.length]=[Z,X,Y]}function F(Z){var Y=M.pv,X=Z.split(".");X[0]=parseInt(X[0],10);X[1]=parseInt(X[1],10)||0;X[2]=parseInt(X[2],10)||0;return(Y[0]>X[0]||(Y[0]==X[0]&&Y[1]>X[1])||(Y[0]==X[0]&&Y[1]==X[1]&&Y[2]>=X[2]))?true:false}function v(ac,Y,ad,ab){if(M.ie&&M.mac){return}var aa=j.getElementsByTagName("head")[0];if(!aa){return}var X=(ad&&typeof ad=="string")?ad:"screen";if(ab){n=null;G=null}if(!n||G!=X){var Z=C("style");Z.setAttribute("type","text/css");Z.setAttribute("media",X);n=aa.appendChild(Z);if(M.ie&&M.win&&typeof j.styleSheets!=D&&j.styleSheets.length>0){n=j.styleSheets[j.styleSheets.length-1]}G=X}if(M.ie&&M.win){if(n&&typeof n.addRule==r){n.addRule(ac,Y)}}else{if(n&&typeof j.createTextNode!=D){n.appendChild(j.createTextNode(ac+" {"+Y+"}"))}}}function w(Z,X){if(!m){return}var Y=X?"visible":"hidden";if(J&&c(Z)){c(Z).style.visibility=Y}else{v("#"+Z,"visibility:"+Y)}}function L(Y){var Z=/[\\\"<>\.;]/;var X=Z.exec(Y)!=null;return X&&typeof encodeURIComponent!=D?encodeURIComponent(Y):Y}var d=function(){if(M.ie&&M.win){window.attachEvent("onunload",function(){var ac=I.length;for(var ab=0;ab<ac;ab++){I[ab][0].detachEvent(I[ab][1],I[ab][2])}var Z=N.length;for(var aa=0;aa<Z;aa++){y(N[aa])}for(var Y in M){M[Y]=null}M=null;for(var X in swfobject){swfobject[X]=null}swfobject=null})}}();return{registerObject:function(ab,X,aa,Z){if(M.w3&&ab&&X){var Y={};Y.id=ab;Y.swfVersion=X;Y.expressInstall=aa;Y.callbackFn=Z;o[o.length]=Y;w(ab,false)}else{if(Z){Z({success:false,id:ab})}}},getObjectById:function(X){if(M.w3){return z(X)}},embedSWF:function(ab,ah,ae,ag,Y,aa,Z,ad,af,ac){var X={success:false,id:ah};if(M.w3&&!(M.wk&&M.wk<312)&&ab&&ah&&ae&&ag&&Y){w(ah,false);K(function(){ae+="";ag+="";var aj={};if(af&&typeof af===r){for(var al in af){aj[al]=af[al]}}aj.data=ab;aj.width=ae;aj.height=ag;var am={};if(ad&&typeof ad===r){for(var ak in ad){am[ak]=ad[ak]}}if(Z&&typeof Z===r){for(var ai in Z){if(typeof am.flashvars!=D){am.flashvars+="&"+ai+"="+Z[ai]}else{am.flashvars=ai+"="+Z[ai]}}}if(F(Y)){var an=u(aj,am,ah);if(aj.id==ah){w(ah,true)}X.success=true;X.ref=an}else{if(aa&&A()){aj.data=aa;P(aj,am,ah,ac);return}else{w(ah,true)}}if(ac){ac(X)}})}else{if(ac){ac(X)}}},switchOffAutoHideShow:function(){m=false},ua:M,getFlashPlayerVersion:function(){return{major:M.pv[0],minor:M.pv[1],release:M.pv[2]}},hasFlashPlayerVersion:F,createSWF:function(Z,Y,X){if(M.w3){return u(Z,Y,X)}else{return undefined}},showExpressInstall:function(Z,aa,X,Y){if(M.w3&&A()){P(Z,aa,X,Y)}},removeSWF:function(X){if(M.w3){y(X)}},createCSS:function(aa,Z,Y,X){if(M.w3){v(aa,Z,Y,X)}},addDomLoadEvent:K,addLoadEvent:s,getQueryParamValue:function(aa){var Z=j.location.search||j.location.hash;if(Z){if(/\?/.test(Z)){Z=Z.split("?")[1]}if(aa==null){return L(Z)}var Y=Z.split("&");for(var X=0;X<Y.length;X++){if(Y[X].substring(0,Y[X].indexOf("="))==aa){return L(Y[X].substring((Y[X].indexOf("=")+1)))}}}return""},expressInstallCallback:function(){if(a){var X=c(R);if(X&&l){X.parentNode.replaceChild(l,X);if(Q){w(Q,true);if(M.ie&&M.win){l.style.display="block"}}if(E){E(B)}}a=false}}}}();