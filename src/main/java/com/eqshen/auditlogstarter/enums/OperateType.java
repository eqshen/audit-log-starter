package com.eqshen.auditlogstarter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 操作类型
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum OperateType {

    CREATE("C","新增"),

    READ("R","读取"),

    UPDATE("U","更新"),

    DELETE("D","删除")
    ;


    private String code;
    private String desc;

}
