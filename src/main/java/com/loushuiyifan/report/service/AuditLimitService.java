package com.loushuiyifan.report.service;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Service;

@Service
public class AuditLimitService {

	@RequiresPermissions("audit:one")
	public String listOne(){
		
		return "one";
	}
	
	@RequiresPermissions("audit:two")
	public String listTwo(){
		
		return "two";
	}
	
	@RequiresPermissions("audit:three")
	public String listThree(){
		
		return "three";
	}
	
	@RequiresPermissions("audit:four")
	public String listFour(){
		
		return "four";
	}
	
	
	
	
	
	
	
}
