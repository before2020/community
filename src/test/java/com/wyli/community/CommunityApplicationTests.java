//package com.wyli.community;
//
//import com.wyli.community.config.AlphaConfig;
//import com.wyli.community.dao.AlphaDao;
//import com.wyli.community.service.AlphaService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
//public class CommunityApplicationTests implements ApplicationContextAware {
//
//	private ApplicationContext applicationContext;
//
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		this.applicationContext = applicationContext;
//	}
//
//	@Test
//	public void testApplicationContext() {
//		AlphaDao alphaDao = applicationContext.getBean("lololo", AlphaDao.class);
//		System.out.println(alphaDao.select());
//	}
//
//	@Test
//	public void testBeanManage() {
//		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
//		System.out.println(alphaService);
//	}
//
//	@Test
//	public void testBeanConfig() {
//		SimpleDateFormat sdf = applicationContext.getBean(SimpleDateFormat.class);
//		System.out.println(sdf.format(new Date()));
//	}
//
//	@Autowired
//	@Qualifier("alphaHibernate")
//	private AlphaDao alphaDao;
//
//	@Autowired
//	private AlphaService alphaService;
//
//	@Test
//	public void testDI() {
//		System.out.println(alphaDao);
//		System.out.println(alphaService);
//	}
//}
