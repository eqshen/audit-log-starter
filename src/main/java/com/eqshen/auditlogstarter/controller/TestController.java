package com.eqshen.auditlogstarter.controller;

import com.alibaba.fastjson.JSONObject;
import com.eqshen.auditlogstarter.annotation.AuditLog;
import com.eqshen.auditlogstarter.enums.OperateType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class TestController {

    @GetMapping("/listUsers")
    @AuditLog(opTargetEL = "",operateType = OperateType.READ,effectRowsEL = "#{data.size()}")
    public JSONObject queryUserList(){
        JSONObject resp = new JSONObject();
        resp.put("code",200);
        resp.put("msg","success");

        List<UserInfo> userList = new ArrayList<>();
        userList.add(new UserInfo("u-007","张三",2,"17621345678"));
        userList.add(new UserInfo("u-001","李四",3,"17621345679"));

        resp.put("data",userList);
        return resp;
    }

    @GetMapping("/getUserInfo")
    @AuditLog(operateType = OperateType.READ,
            opTargetEL = "查询用户信息：#{userId}",
            effectRowsEL = "#{data == null?0:1}")
    public JSONObject getUser(String userId){
        JSONObject resp = new JSONObject();
        resp.put("code",200);
        resp.put("msg","success");
        resp.put("data",new UserInfo("u-007","张三",2,"17621345678"));

        return resp;
    }


    @PostMapping("/operateUser")
    @AuditLog(operateType = OperateType.UPDATE,
            opTargetEL = "用户id: #{userId}", //必须跟 userInfo对象中的属性名称一致
            effectRowsEL = "#{code == 200?1:0}")
    public JSONObject operateUser(@RequestBody UserInfo userInfo){

        //用于测试异常情况
        if(Objects.equals(userInfo.getUserId(), "1234")){
            throw new RuntimeException("用户不存在");
        }

        JSONObject resp = new JSONObject();
        resp.put("code",200);
        resp.put("msg","success");
        return resp;
    }
}
