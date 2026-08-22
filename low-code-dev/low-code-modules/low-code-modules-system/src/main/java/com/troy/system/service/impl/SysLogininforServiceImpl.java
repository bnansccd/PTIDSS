package com.troy.system.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.tenant.TenantManager;
import com.troy.common.core.context.SecurityContextHolder;
import com.troy.common.core.domain.ResultVO;
import com.troy.common.core.enums.DictValueEnums;
import com.troy.common.core.utils.BigDecimalUtils;
import com.troy.common.core.utils.DateUtils;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.VO.PageVO;
import com.troy.common.datasource.utils.PageUtils;
import com.troy.system.api.domain.DTO.SysLogininforDTO;
import com.troy.system.api.domain.VO.SysLogininforVO;
import com.troy.system.dao.SysLogininforDao;
import com.troy.system.dao.SysUserDao;
import com.troy.system.domain.DTO.DateRangeDTO;
import com.troy.system.domain.DTO.SysLogininfoQueryDTO;
import com.troy.system.domain.VO.LoginTimesAndChainVO;
import com.troy.system.entity.SysLogininforEntity;
import com.troy.system.entity.SysUserEntity;
import com.troy.system.service.SysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 系统访问记录 服务实现类
 * </p>
 *
 * @author zhuqing
 * @since 2022/08/08 17:26:58
 */
@Service
public class SysLogininforServiceImpl implements SysLogininforService {

    @Autowired
    private SysLogininforDao sysLogininforDao;

    @Autowired
    private SysUserDao userDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVO insertLogininfor(SysLogininforDTO dto) {
        try {
            TenantManager.ignoreTenantCondition();
            SysLogininforEntity sysLogininforEntity = new SysLogininforEntity();
            BeanUtils.copyProperties(dto, sysLogininforEntity);
            this.sysLogininforDao.save(sysLogininforEntity);
            if (dto.getUserId() != null){
                SysUserEntity sysUserEntity = userDao.getById(dto.getUserId());
                if (StringUtils.isNotNull(sysUserEntity)){
                    sysUserEntity.setLastLoginIp(dto.getLoginIp());
                    sysUserEntity.setLastLoginTime(new Date());
                    sysUserEntity.setLoginTimes(sysUserEntity.getLoginTimes() == null ? 0:sysUserEntity.getLoginTimes()+1);
                    userDao.updateById(sysUserEntity);
                }
            }
        }finally {
            TenantManager.restoreTenantCondition();
        }
        return ResultVO.success();
    }

    @Override
    public PageVO<SysLogininforVO> getSysLogininforList(SysLogininfoQueryDTO dto) {
        Page page = this.sysLogininforDao.getSysLogininforPage(dto);
        return PageUtils.convertPageVo(page, SysLogininforVO.class);
    }

    @Override
    public SysLogininforVO getSysLogininforById(Long id) {
        SysLogininforEntity sysLogininforEntity = this.sysLogininforDao.getById(id);
        return copyLoginInfoVO(sysLogininforEntity);
    }

    @Override
    public LoginTimesAndChainVO getTimesAndChain(DateRangeDTO dto) {
        String userName = SecurityContextHolder.getUserName();
        LoginTimesAndChainVO vo = new LoginTimesAndChainVO();
        if (StringUtils.isEmpty(userName)){
            return vo;
        }
        Date nowDate = DateUtils.getNowDate();

        Date startDate = getDate(dto.getStatisticType(), dto.getStartDate(), nowDate, true);
        Date endDate = getDate(dto.getStatisticType(), dto.getEndDate(), nowDate, false);
        List<SysLogininforEntity> now = sysLogininforDao.getSysLogininforList(userName, startDate, endDate);
        vo.setLoginTimes(now.size());
        //查询上一个周期
        int day = DateUtils.getDay(startDate, endDate) + 1;
        Date startDate1 = DateUtils.adjustNumber(startDate, -day);
        Date endDate1 = DateUtils.adjustNumber(endDate, -day);
        List<SysLogininforEntity> before = sysLogininforDao.getSysLogininforList(userName, startDate1, endDate1);
        if (StringUtils.isNotEmpty(before)) {
            vo.setLoginChain(
                    BigDecimalUtils.divide(BigDecimal.valueOf((now.size() - before.size())),
                                    BigDecimal.valueOf(before.size()), 4, BigDecimal.ROUND_DOWN)
                            .multiply(BigDecimal.valueOf(100)).stripTrailingZeros()
            );
        }
        return vo;
    }


    /**
     * 根据统计规则得到时间
     *
     * @param statisticType
     * @param date
     * @param nowDate
     * @param isStartDate
     * @return
     */
    private Date getDate(String statisticType, Date date, Date nowDate, boolean isStartDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        if (StringUtils.isNull(date)) {
            if (StringUtils.equals(statisticType, DictValueEnums.SLJZ_STATISTICS_TYPE_0.getCode())) {
                if (isStartDate) {
                    date = DateUtils.getStartTimeOfDay(DateUtils.getYesterday(nowDate));
                } else {
                    date = DateUtils.getEndTimeOfDay(DateUtils.getYesterday(nowDate));
                }
            } else if (StringUtils.equals(statisticType, DictValueEnums.SLJZ_STATISTICS_TYPE_1.getCode())) {
                if (dayOfWeek == 2 || dayOfWeek == 1) {
                    if (isStartDate) {
                        date = DateUtils.getStartTimeOfDay(DateUtils.getWeekFirstDay(DateUtils.adjustNumber(nowDate, -2)));
                    } else {
                        date = DateUtils.getEndTimeOfDay(DateUtils.getWeekLastDay(DateUtils.adjustNumber(nowDate, -2)));
                    }
                } else {
                    if (isStartDate) {
                        date = DateUtils.getStartTimeOfDay(DateUtils.getWeekFirstDay(nowDate));
                    } else {
                        date = DateUtils.getEndTimeOfDay(DateUtils.getWeekLastDay(nowDate));
                    }
                }
            } else if (StringUtils.equals(statisticType, DictValueEnums.SLJZ_STATISTICS_TYPE_2.getCode())) {
                if (dayOfMonth == 1) {
                    if (isStartDate) {
                        date = DateUtils.getStartTimeOfDay(DateUtils.getMonthFirstDay(DateUtils.adjustMonth(nowDate, -1)));
                    } else {
                        date = DateUtils.getEndTimeOfDay(DateUtils.getMonthLastDay(DateUtils.adjustMonth(nowDate, -1)));
                    }
                } else {
                    if (isStartDate) {
                        date = DateUtils.getStartTimeOfDay(DateUtils.getMonthFirstDay(nowDate));
                    } else {
                        date = DateUtils.getEndTimeOfDay(DateUtils.getMonthLastDay(nowDate));
                    }
                }
            } else if (StringUtils.equals(statisticType, DictValueEnums.SLJZ_STATISTICS_TYPE_3.getCode())) {
                if (dayOfYear == 1) {
                    if (isStartDate) {
                        date = DateUtils.getStartTimeOfDay(DateUtils.getYearFirstDay(DateUtils.adjustYear(nowDate, -1)));
                    } else {
                        date = DateUtils.getEndTimeOfDay(DateUtils.getYearLastDay(DateUtils.adjustYear(nowDate, -1)));
                    }
                } else {
                    if (isStartDate) {
                        date = DateUtils.getStartTimeOfDay(DateUtils.getYearFirstDay(nowDate));
                    } else {
                        date = DateUtils.getEndTimeOfDay(DateUtils.getYearLastDay(nowDate));
                    }
                }
            }
        }
        return date;
    }

    private SysLogininforVO copyLoginInfoVO(SysLogininforEntity entity) {
        if (StringUtils.isNull(entity)) {
            return null;
        }
        SysLogininforVO vo = new SysLogininforVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

}
