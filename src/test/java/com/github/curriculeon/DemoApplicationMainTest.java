package com.github.curriculeon;

import org.junit.Test;

public class DemoApplicationMainTest {

    @Test
    public void main_skippedWhenSystemPropertySet() {
        System.setProperty("skip.spring.boot", "true");
        try {
            DemoApplication.main(new String[]{});
        } finally {
            System.clearProperty("skip.spring.boot");
        }
    }

}
