package com.loushuiyifan;

import com.loushuiyifan.system.service.OrganizationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppTests {

    @Autowired
    OrganizationService organizationService;

    @Test
    public void importDataFromCodeListTax() {
        organizationService.importDataFromCodeListTax();
    }

}
