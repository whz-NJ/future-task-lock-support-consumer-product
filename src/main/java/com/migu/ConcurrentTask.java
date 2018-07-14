package com.migu;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author whz
 * @create 2018-07-14 19:25
 * @desc TODO: add description here
 **/
public class ConcurrentTask
{
    /*
    //FutireTask提供的获取结果的方法
    public Object get() {
        if(执行中) {
            while(true) {
                // 1. 如果执行完毕则跳出循环
                if(执行结束) 跳出
                // 2. 把用户线程信息存到FutureTask的waiters链表中
                new WaitNode();
                // 3. 用户线程挂起，编程消费者，等待FutureTask跑完后唤醒
                LockSupport.park(Thread.currentThread())
             }
        }
        // 返回结果
        // 如果callable执行没有问题，返回all方法的结果，否则返回异常
        return result;
    }

    FutureTask的run()方法（生产者执行的方法）的伪码：
    public void run() {
        // 判断状态
        if(!未执行) return;
        try {
            result = callable.call();
            //则修改状态为正常结束
            finishCompletion(); //通知消费者
        } catch(Exception ex) {
            // 执行出现异常，则修改状态为异常结束
            setException(ex);
            finishCompletion(); //通知消费者
    }
    //通知消费者的方法
    public void finishCompletion() {
        for(Node node : waiters) {
            LockSupport.unpark(node.thread);
        }
    }
     */
    private static final Logger logger = LoggerFactory.getLogger(SequenceTask.class);

    @Autowired RestTemplate restTemplate;

    public Object getUserInfo(final String userId)
            throws ExecutionException, InterruptedException
    {
        Callable<JSONObject> userInfoCallable = new Callable<JSONObject>()
    {
        @Override public JSONObject call() throws Exception
        {
            // 1. 调用获取用户基础信息的http接口
            long userInfoTime = System.currentTimeMillis();
            String value = restTemplate
                    .getForObject("http://www.tony.com/userinfo-api/get?userId=" + userId,
                            String.class);
            JSONObject userInfo = JSONObject.parseObject(value);
            logger.error(
                    "userinfo-ai用户基本信息()调用耗时为" + (System.currentTimeMillis() - userInfoTime));
            return userInfo;
        }
    };
        Callable<JSONObject> intergralInfoCallable = new Callable<JSONObject>()
        {

            @Override public JSONObject call() throws Exception
            {
                //2. 调用用户积分信息接口
                long integralApiTime = System.currentTimeMillis();
                String intergral = restTemplate
                        .getForObject("http://www.tony.com/integral-api/get?userId=" + userId,
                                String.class);
                JSONObject intergralInfo = JSONObject.parseObject(intergral);

                logger.error(
                        "integral-ai用户积分信息()调用耗时为" + (System.currentTimeMillis() - integralApiTime));
                return intergralInfo;
            }
        };
        FutureTask<JSONObject> userInfoFutureTask = new FutureTask<>(userInfoCallable);
        FutureTask<JSONObject> intergralInfoFutureTask = new FutureTask<>(intergralInfoCallable);
        // 运行 FutureTask（FutureTask实现了Runnable接口）
        new Thread(userInfoFutureTask).start();
        new Thread(intergralInfoFutureTask).start();
        // 构造一个json对象
        JSONObject result = new JSONObject();
        //用户主线程FutureTask等待执行完毕，在get方法获取到返回值前，这个用户线程会停在这里，不会往下执行
        result.putAll(userInfoFutureTask.get());
        result.putAll(intergralInfoFutureTask.get());
        return result;


    }
}