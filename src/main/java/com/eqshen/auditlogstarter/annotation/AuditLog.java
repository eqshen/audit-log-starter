package com.eqshen.auditlogstarter.annotation;

import com.eqshen.auditlogstarter.enums.OperateType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AuditLog {

    /**
     * 操作类型
     * @return
     */
    OperateType operateType();

    /**
     * 操作对象表达式
     * @return
     */
    String opTargetEL();

    /**
     * 影响行数表达式
     * @return
     */
    String effectRowsEL();

}
