import { SKF } from "./skf.min.js";
var logLevel = 0;
var pin = "12345678";
var rndLen = 16;
var signdata = "YWJjMTIzIUAj";
var devName = ""; // 设备名称
var hDev = 0; // 设备句柄
var hHash = 0; // 杂凑句柄
var containerName = ""; // 容器名称
var pubKey = ""; // Base64 编码的公钥
var sign = ""; // 签名值
// var cipherData = ""; // 密文值
var genRandom = ''

const validUkey = () => {
    var skf = new SKF("swskfapi");
    var json_rst = skf.SKF_EnumDev();
    if (json_rst.status == 0) {
        return !!json_rst.value[0]
    } else {
        return false;
    }
}
const getUkey = (selfPin:any) => {
    if (selfPin) {
        pin = selfPin
    }
    try {
        // ################ 初始化UKey：创建SKF对象 + 设置日志等级 + 获取设备名称 + 连接设备 + 验证口令 + 获取容器名称
        // 创建 SKF 接口对象
        var skf = new SKF("swskfapi");
        // 设置 SKF 接口日志输出等级
        skf.SetLogLevel(logLevel);
        // 获取设备名称
        var json_rst = skf.SKF_EnumDev();
        if (json_rst.status == 0) {
            devName = json_rst.value[0];
        } else {
            throw "获取设备名称错误：" + json_rst.msg + "(" + json_rst.status + ")";
        }
        // 连接设备，获取设备句柄 hDev，业务接口都需要 hDev 参数
        json_rst = skf.SKF_ConnectDev(devName);
        if (json_rst.status == 0) {
            hDev = json_rst.value;
        } else {
            throw "连接设备错误：" + json_rst.msg + "(" + json_rst.status + ")";
        }
        // 验证用户口令
        json_rst = skf.SKF_VerifyPIN(hDev, pin, 1);
        if (json_rst.status == 0) {
        } else {
            throw "验证用户口令错误：" + json_rst.msg + "(" + json_rst.status + ")";
        }
        // 获取设备的容器名称 containerName，业务接口都需要 containerName 参数
        json_rst = skf.SKF_EnumContainer(hDev);
        if (json_rst.status == 0 && json_rst.value.length > 0) {
            containerName = json_rst.value[0];
        } else {
            throw "枚举容器错误：" + json_rst.msg + "(" + json_rst.status + ")";
        }
        
        // ################ UKey生成随机数
        // 建议用户将 UKey生成的随机数 作为签名数据的一部分
        json_rst = skf.SKF_GenRandom(hDev, rndLen)
        if (json_rst.status == 0) {
            genRandom = json_rst.value
        } else {
            throw "UKey生成随机数错误:" + json_rst.msg + "(" + json_rst.status + ")";
        }
        
        // ################ UKey签名共3步：导出UKey公钥 + 计算数据摘要值 + 对摘要值签名 + 将签名结果转为DER编码
        // 导出UKey公钥（用于计算摘要）
        json_rst = skf.SKF_ExportPublicKey(hDev, containerName, 1, 0); // 入参固定
        if (json_rst.status == 0) {
            pubKey = json_rst.value;
        } else {
            throw "导出UKey公钥错误:" + json_rst.msg + "(" + json_rst.status + ")";
        }
        // 签名前，先计算数据摘要值
        json_rst = skf.SKF_DigestInit(hDev, 1, pubKey, "1234567812345678", 0) // 入参固定
        if (json_rst.status == 0) {
            hHash = json_rst.value;
            json_rst = skf.SKF_Digest(hHash, genRandom, 1); // 入参固定
            if (json_rst.status == 0) {
                signdata = json_rst.value;
            } else {
                throw "计算数据摘要值错误:" + json_rst.msg + "(" + json_rst.status + ")";
            }
        } else {
            throw "计算数据摘要值错误:" + json_rst.msg + "(" + json_rst.status + ")";
        }
        // 对摘要值签名
        json_rst = skf.SKF_ECCSignData(hDev, containerName, signdata);
        if (json_rst.status == 0) {
            sign = json_rst.value;
        } else {
            throw "签名错误：" + json_rst.msg + "(" + json_rst.status + ")";
        }
        // 将签名结果转为DER编码（必做）
        try {
            sign = convertSignToDer(sign);
        } catch (error) {
            throw "转换失败：" + error.message;
        }
    } catch (e) {
        // 打印错误日志
    } finally {
        // 断开设备连接
        var json_rst = skf.SKF_DisConnectDev(hDev);
        if (json_rst.status == 0) {
        } else {
        }
    }
    return {
        sign,
        containerName,
        genRandom,
        pubKey
    }
}

/**
		 * 将原始签名值转换为 DER 编码格式
		 * @param {string} base64Sign - Base64 编码的原始签名值（128字节）
		 * @returns {string} Base64 编码的 DER 格式签名值
		 */
function convertSignToDer(base64Sign) {
    try {
        // Base64 解码
        const signvalue = Uint8Array.from(atob(base64Sign), c => c.charCodeAt(0));
        
        // 调用 DER 转换方法
        const dersign = getSignValueToDer(signvalue);
        
        if (!dersign) {
            throw new Error('无效的签名值：签名长度不是128字节');
        }
         
        // Base64 编码返回
        return btoa(String.fromCharCode(...dersign));
    } catch (error) {
        console.error('签名转换失败:', error);
        throw error;
    }
}

/**
 * 将原始签名值转换为 DER 编码（核心逻辑）
 * @param {Uint8Array} signvalue - 原始签名值（128字节）
 * @returns {Uint8Array|null} DER 编码的签名值，如果无效则返回 null
 */
function getSignValueToDer(signvalue) {
    if (signvalue.length !== 128) {
        // 传入签名值的长度不是128字节，按照无效签名值处理
        return null;
    }
    
    // 判断 r/s 前面是否需要补 0x00
    let rpad = 0;
    let spad = 0;
    const signvalue0 = signvalue[32];
    if (signvalue0 < 0) {
        rpad = 1;
    }
    
    const signvalue32 = signvalue[96];
    if (signvalue32 < 0) {
        spad = 1;
    }
    
    // der 编码数据的长度为: R02+RL+Rpad+R+S02+SL+Spad+S 的总长度
    const dataLen = 1 + 1 + rpad + 32 + 1 + 1 + spad + 32;
    
    // der 编码后数据的总长度
    const derLength = 1 + 1 + dataLen;
    let point = 0;
    const derSignValue = new Uint8Array(derLength);
    
    derSignValue[point] = 0x30; // SEQUENCE
    point++;
    derSignValue[point] = dataLen; // Length
    point++;
    derSignValue[point] = 0x02; // INTEGER (r)
    point++;
    derSignValue[point] = 32 + rpad; // r length
    point++;
    
    if (rpad === 1) {
        derSignValue[point] = 0x00; // padding
        point++;
    }
    
    // 复制 r 值（第33-64字节）
    derSignValue.set(signvalue.slice(32, 64), point);
    point += 32;
    
    derSignValue[point] = 0x02; // INTEGER (s)
    point++;
    derSignValue[point] = 32 + spad; // s length
    point++;
    
    if (spad === 1) {
        derSignValue[point] = 0x00; // padding
        point++;
    }
    
    // 复制 s 值（第97-128字节）
    derSignValue.set(signvalue.slice(96, 128), point);
    
    return derSignValue;
}


export {
    getUkey,
    validUkey
}