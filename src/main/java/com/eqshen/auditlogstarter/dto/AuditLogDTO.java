package com.eqshen.auditlogstarter.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志对象
 */
@Data
public class AuditLogDTO {

    private LocalDateTime opTime;

    /**
     * @see com.eqshen.auditlogstarter.enums.OperateType
     */
    private String operateType;

    /**
     * 操作人账号或id
     */
    private String opAccount;

    /**
     * 操作对象，如 订单号，用户id等
     */
    private String opTarget;

    /**
     * 操作结果，影响行数等
     */
    private String opResult;
}
