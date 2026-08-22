package com.troy.system.api;

import com.troy.system.api.domain.DTO.SysDepartDTO;
import com.troy.system.api.domain.VO.SysDepartVO;
import com.troy.system.api.factory.RemoteSysDepartFallbackFactory;
import com.troy.common.core.constant.SecurityConstants;
import com.troy.common.core.constant.ServiceNameConstants;
import com.troy.common.core.constant.UrlConstants;
import com.troy.common.core.domain.ResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chenxl
 */
@FeignClient(contextId = "remoteSysDepartService", path = UrlConstants.RPC_RESTFUL, name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSysDepartFallbackFactory.class)
public interface RemoteSysDepartService {

    /**
     * 获取部门信息
     * @param id
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart/{id}")
    ResultVO<SysDepartVO> findSysDepartById(@PathVariable("id") Long id);


    /**
     * 批量查询
     * @param source
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart")
    ResultVO<List<SysDepartVO>> findAll(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 保存
     * @param dto
     * @param source
     * @return
     */
    @PostMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart")
    ResultVO insertSysDepart(@RequestBody @Validated SysDepartDTO dto, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 保存
     * @param ids
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart/byIds")
    ResultVO<List<SysDepartVO>> findByIdIn(@RequestParam("ids") List<Long> ids, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过id查询部门
     *
     * @param id
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1 + "sysDepart/{id}")
    ResultVO<SysDepartVO> findById(@PathVariable("id") Long id, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过部门名称查询
     * @param deptName
     * @return
     */
    @GetMapping(UrlConstants.RESTFUL_VERSION_V1+"sysDepart/findByDeptNameLike")
    ResultVO<List<SysDepartVO>> findByDeptNameLike(@RequestParam("deptName") String deptName);
}
