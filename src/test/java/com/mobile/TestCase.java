package com.mobile;

import com.mobile.core.config.AppiumDriverConfig;
import org.testng.annotations.Test;

import java.io.IOException;

public class TestCase {

    @Test
    public void check() throws IOException {
        System.out.println("hello"+ AppiumDriverConfig.getInstance().getAppiumServerTimeOut());
    }
}
