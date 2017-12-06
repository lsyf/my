package org.springframework;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author 漏水亦凡
 * @date 2017/12/5
 */
public class SpringTest {

    @Test
    public void test1() {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean.xml");
        SayService sayService = (SayService) applicationContext.getBean("test");
        sayService.say();
    }
}
