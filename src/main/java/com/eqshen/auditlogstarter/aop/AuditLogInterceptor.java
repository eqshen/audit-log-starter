package com.eqshen.auditlogstarter.aop;

import com.alibaba.fastjson.JSONObject;
import com.eqshen.auditlogstarter.annotation.AuditLog;
import com.eqshen.auditlogstarter.dto.AuditLogAopMetaDTO;
import com.eqshen.auditlogstarter.dto.AuditLogDTO;
import com.eqshen.auditlogstarter.util.AopUtil;
import com.eqshen.auditlogstarter.util.SpELUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AuditLogInterceptor {

    @Pointcut("@annotation(com.eqshen.auditlogstarter.annotation.AuditLog)")
    public void pointcut(){
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {

        Object result = null;
        final AuditLogDTO auditLogDTO;
        final AuditLogAopMetaDTO auditLogAopMetaDTO = this.beforeProcess(point);

        try{
            result = point.proceed();
        }finally {
            auditLogDTO = this.afterProcess(auditLogAopMetaDTO, result);

            //异步推送kafka
            this.asyncSendToKafka(auditLogDTO);
        }

        return result;
    }

    private AuditLogAopMetaDTO beforeProcess(ProceedingJoinPoint point){
        AuditLogAopMetaDTO auditLogAopMetaDTO = new AuditLogAopMetaDTO();
        try{
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            if (sra == null) return null;

            final HttpServletRequest request = sra.getRequest();

            String methodType = request.getMethod();;
            Method method = AopUtil.getMethod(point);
            AuditLog auditLog = method.getAnnotation(AuditLog.class);

            auditLogAopMetaDTO.setAuditLog(auditLog);
            auditLogAopMetaDTO.setHttpMethodType(methodType);
            auditLogAopMetaDTO.setHttpServletRequest(request);
            auditLogAopMetaDTO.setReqParam(AopUtil.getParamsNameAndValue(point));
            auditLogAopMetaDTO.setPostParam(AopUtil.getPostParam(point));


        }catch (Exception e){
            log.error("预处理失败",e);
        }
        return auditLogAopMetaDTO;
    }

    private AuditLogDTO afterProcess(AuditLogAopMetaDTO aopMeta, Object result){
        if(aopMeta == null){
            return null;
        }
        final AuditLog auditLog = aopMeta.getAuditLog();

        Object opTarget = SpELUtil.explainEl(aopMeta.getPostParam(),auditLog.opTargetEL());

        if(opTarget == null){
            opTarget = SpELUtil.explainEl(aopMeta.getReqParam(),auditLog.opTargetEL());
        }

        String effectRows = String.valueOf(SpELUtil.explainEl(result,auditLog.effectRowsEL()));

        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setOpTime(LocalDateTime.now());
        auditLogDTO.setOperateType(auditLogDTO.getOperateType());
        auditLogDTO.setOpAccount(null);//一般从Context中获取当前登录的用户信息
        auditLogDTO.setOpResult(effectRows);
        auditLogDTO.setOpTarget(String.valueOf(opTarget==null?"":opTarget));

        return auditLogDTO;

    }

    private void asyncSendToKafka(AuditLogDTO auditLogDTO){
        if(auditLogDTO == null) return;
        //推荐使用线程数池操作,此处仅输出日志
        log.info("======> 操作日志：{}", JSONObject.toJSONString(auditLogDTO));
    }
}

