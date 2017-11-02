package com.loushuiyifan;

import com.loushuiyifan.report.serv.CodeListTaxService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 漏水亦凡
 * @date 2017/9/18
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CodeListTaxServiceTest {

    @Autowired
    CodeListTaxService codeListTaxService;


    @Test
    public void updateCodeListTaxPath() {
        codeListTaxService.updateCodeListTaxPath();
    }



}
