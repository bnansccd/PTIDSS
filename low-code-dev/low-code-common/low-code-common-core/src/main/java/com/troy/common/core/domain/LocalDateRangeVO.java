package com.troy.common.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author sym
 * @since 2024/9/18 11:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalDateRangeVO {

    private LocalDate startDate;

    private LocalDate endDate;

}
