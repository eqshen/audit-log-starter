package com.eqshen.auditlogstarter.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

@Slf4j
public class SpELUtil {
    //thread-safe
    private static final SpelExpressionParser parser = new SpelExpressionParser();

    private static final TemplateParserContext parserContext = new TemplateParserContext("#{", "}");



    public static Object explainEl(Object obj, String spELExpression){
        if (StringUtils.isEmpty(spELExpression) || obj == null) {
            return null;
        }

        try {
            // spring的表达式上下文对象
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.addPropertyAccessor(new MapAccessor());//增加对map的访问支持
            context.setRootObject(obj);


            Expression expression = parser.parseExpression(spELExpression, parserContext);
            return expression.getValue(context);
        } catch (Exception e) {
            log.error("SpEL解析失败, 表达式: {}, 输入: {}", spELExpression, JSONObject.toJSONString(obj), e);
        }
        return null;
    }

}
