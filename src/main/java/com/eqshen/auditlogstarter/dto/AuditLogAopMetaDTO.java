package com.eqshen.auditlogstarter.dto;

import com.eqshen.auditlogstarter.annotation.AuditLog;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 存储切面过程中一些元数据
 */
@Data
public class AuditLogAopMetaDTO {
    private HttpServletRequest httpServletRequest;

    private AuditLog auditLog;

    private String httpMethodType;

    /**
     * Get请求参数
     */
    private Map<String,Object> reqParam;

    /**
     * Post请求参数
     */
    private Object postParam;
}
