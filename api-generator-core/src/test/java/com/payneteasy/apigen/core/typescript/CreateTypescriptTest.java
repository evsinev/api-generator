package com.payneteasy.apigen.core.typescript;

import com.payneteasy.apigen.core.typescript.example.IExampleService;
import org.junit.Test;

public class CreateTypescriptTest {

    @Test
    public void test() {
        CreateTypescript createTypescript = new CreateTypescript("/api");
        String           typescript       = createTypescript.generateTypescript(IExampleService.class);
        System.out.println("typescript = \n" + typescript);
    }

}