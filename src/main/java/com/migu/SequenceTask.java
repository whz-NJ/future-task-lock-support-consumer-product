package com.migu;

import com.alibaba.fastjson.JSONObject;
// import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author whz
 * @create 2018-07-14 19:04
 * @desc ww
 **/
@Service public class SequenceTask
{
    private static final Logger logger = LoggerFactory.getLogger(SequenceTask.class);

    @Autowired RestTemplate restTemplate;

    public Object getUserInfo(String userId)
    {
        // 1. 调用获取用户基础信息的http接口
        long userInfoTime = System.currentTimeMillis();
        String value = restTemplate
                .getForObject("http://www.tony.com/userinfo-api/get?userId=" + userId,
                        String.class);
        JSONObject userInfo = JSONObject.parseObject(value);
        logger.error(
                "userinfo-ai用户基本信息()调用耗时为" + (System.currentTimeMillis() - userInfoTime));
        //2. 调用用户积分信息接口
        long integralApiTime = System.currentTimeMillis();
        String intergral = restTemplate
                .getForObject("http://www.tony.com/integral-api/get?userId=" + userId,
                        String.class);
        JSONObject intergralInfo = JSONObject.parseObject(intergral);

        logger.error(
                "integral-ai用户积分信息()调用耗时为" + (System.currentTimeMillis() - userInfoTime));
        //3. 构造一个json对象
        JSONObject result = new JSONObject();
        result.putAll(userInfo);
        result.putAll(intergralInfo);
        return result;


    }
}
