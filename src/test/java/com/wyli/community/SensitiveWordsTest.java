package com.wyli.community;

import com.wyli.community.util.SensitiveWordsFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveWordsTest {

    @Autowired
    private SensitiveWordsFilter filter;

    @Test
    public void testFilter() {
        String s = filter.filter("ABC阿福赌博速度快嫖娼 东方 vv 天堂地狱 晚饭332E4RT法轮功FSDG");
        System.out.println(s);
    }
}
