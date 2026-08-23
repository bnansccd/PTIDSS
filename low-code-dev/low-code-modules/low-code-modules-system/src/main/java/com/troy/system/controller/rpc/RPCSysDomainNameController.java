package com.troy.system.controller.rpc;

import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.utils.ServletUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.security.annotation.InnerAuth;
import com.troy.system.api.domain.VO.BasicInfoVO;
import com.troy.system.api.domain.VO.SysDomainNameVO;
import com.troy.system.service.SysDomainNameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @Auther: zhuqing
 * @Date: 2023/10/8 16:16:01
 * @Description: RPCSysDomainNameController
 * @Version: 1.0.0
 */
@Api(tags = "RPC-租户域名管理")
@RestController
@RequestMapping(UrlConstants.RPC_RESTFUL)
public class RPCSysDomainNameController {

    @Autowired
    private SysDomainNameService sysDomainNameService;

    @ApiOperation(value = "查询域名基础信息")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "domainName/info")
    public ResultVO<BasicInfoVO> findDomainNameInfo(HttpServletRequest request) {
        BasicInfoVO vo = null;
        String domainName = ServletUtils.getDomainName(request);
        if (StringUtils.isNotBlank(domainName)) {
            vo = this.sysDomainNameService.findByDomainNameOrUniversalDomainName(domainName);
        }
        return ResultVO.success(vo);
    }

    @InnerAuth
    @ApiOperation(value = "通过域名查询域名基础信息")
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "domainName")
    public ResultVO<SysDomainNameVO> findByDomainNameOrUniversalDomainName(@RequestParam String domainName) {
        SysDomainNameVO sysDomainNameVO=null;
        BasicInfoVO basicInfoVO = this.sysDomainNameService.findByDomainNameOrUniversalDomainName(domainName);
        if (StringUtils.isNotNull(basicInfoVO)){
             sysDomainNameVO = basicInfoVO.getSysDomainNameVO();
        }
        return ResultVO.success(sysDomainNameVO);
    }
}
