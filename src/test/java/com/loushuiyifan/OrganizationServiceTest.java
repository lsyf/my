package com.loushuiyifan;

import com.loushuiyifan.system.service.OrganizationService;
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
public class OrganizationServiceTest {

    @Autowired
    OrganizationService organizationService;


    @Test
    public void importDataFromCodeListTax() {
        organizationService.importDataFromCodeListTax();
    }

    @Test
    public void importDataFromUserCompany() {
        organizationService.importDataFromUserCompany();
    }


}
