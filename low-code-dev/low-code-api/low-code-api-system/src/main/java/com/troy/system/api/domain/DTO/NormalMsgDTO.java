package com.troy.system.api.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author sym
 * @since 2025/11/26 下午4:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NormalMsgDTO {

    List<String> phoneNums;

    String msgMark;

    List<String> params;


}
