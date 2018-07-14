package com.migu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test public void testPark() throws InterruptedException
    {
        System.out.println("这是一个Allen老师和微信附近女神，在酒店约会的故事");
        final Thread thread1 = new Thread(new Runnable()
        {
            @Override public void run()
            {
                try
                {
                    Thread.sleep(1000);
                    System.out.println("Allen老师到了酒店门口");
                    //强制线程进入等待状态，不执行后续代码
                    LockSupport.park();
                    System.out.println("确认过眼神，遇见对的人，在酒店度过了一个美好的夜晚");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        final Thread thread2 = new Thread(new Runnable()
        {
            @Override public void run()
            {
                try
                {
                    System.out.println("女神到了酒店门口");
                    //强制线程进入等待状态，不执行后续代码
                    LockSupport.unpark(thread1);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

}
