package com.troy.common.datasource.utils;

import com.mybatisflex.core.paginate.Page;
import com.troy.common.core.utils.StringUtils;
import com.troy.common.core.utils.bean.BeanUtils;
import com.troy.common.core.web.DTO.PageDTO;
import com.troy.common.core.web.VO.PageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: zhuqing
 * @Date: 2023/8/31 11:11:16
 * @Description: PageUtils
 * @Version: 1.0.0
 */
public class PageUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(PageUtils.class);

    public static PageVO convertPageVo(Page page) {
        PageVO vo = new PageVO();
        vo.setRecords(page.getRecords());
        vo.setTotal(page.getTotalRow());
        vo.setCurrent(page.getPageNumber());
        vo.setSize(page.getPageSize());
        vo.setPages(page.getTotalPage());
        return vo;
    }

    /**
     *
     * @param page
     * @param clazz
     * @return
     */
    public static <T> PageVO<T> convertPageVo(Page page, Class<T> clazz) {
        PageVO<T> vo = new PageVO();
        List sources = page.getRecords();
        if (StringUtils.isNotEmpty(sources)) {
            List targets = new ArrayList();
            try {
                Object target = null;
                for (Object source : sources) {
                    target = clazz.newInstance();
                    BeanUtils.copyProperties(source, target);
                    targets.add(target);
                }
            }catch (Exception e){
                LOGGER.error("分页类型转换错误");
            }
            vo.setRecords(targets);
        }
        vo.setTotal(page.getTotalRow());
        vo.setCurrent(page.getPageNumber());
        vo.setSize(page.getPageSize());
        vo.setPages(page.getTotalPage());
        return vo;
    }

    /**
     * 内存分页
     *
     * @param pageDto
     * @param coll
     * @return
     */
    public static PageVO pageVo(Page pageDto, List<?> coll) {
        PageVO pageVO = new PageVO();
        long current = pageDto.getPageNumber();
        long size = pageDto.getPageSize();
        long totalElements = 0L;
        List datas = new ArrayList();
        if (StringUtils.isNotEmpty(coll)) {
            totalElements = coll.size();
            datas = coll.stream().skip((current * size) - size).limit(size).collect(Collectors.toList());
        }
        try {
            pageVO.setCurrent(current);
            pageVO.setSize(size);
            //计算总页数
            long totalPage = current;
            if (totalElements % size == 0) {
                totalPage = totalElements / size;
            } else {
                totalPage = totalElements / size + 1;
            }
            pageVO.setTotal(totalElements);
            pageVO.setPages(totalPage);
            pageVO.setSize(datas.stream().count());
            pageVO.setRecords(datas);
        } catch (Exception e) {
            LOGGER.error("分页数据类型转换失败", e);
        }
        return pageVO;
    }

    /**
     * 内存分页
     *
     * @param pageDto
     * @param coll
     * @return
     */
    public static PageVO pageVo(PageDTO pageDto, List<?> coll) {
        PageVO pageVO = new PageVO();
        long current = pageDto.getCurrent();
        long size = pageDto.getSize();
        long totalElements = 0L;
        List datas = new ArrayList();
        if (StringUtils.isNotEmpty(coll)) {
            totalElements = coll.size();
            datas = coll.stream().skip((current * size) - size).limit(size).collect(Collectors.toList());
        }
        try {
            pageVO.setCurrent(current);
            pageVO.setSize(size);
            //计算总页数
            long totalPage = current;
            if (totalElements % size == 0) {
                totalPage = totalElements / size;
            } else {
                totalPage = totalElements / size + 1;
            }
            pageVO.setTotal(totalElements);
            pageVO.setPages(totalPage);
            pageVO.setSize(datas.stream().count());
            pageVO.setRecords(datas);
        } catch (Exception e) {
            LOGGER.error("分页数据类型转换失败", e);
        }
        return pageVO;
    }
}
