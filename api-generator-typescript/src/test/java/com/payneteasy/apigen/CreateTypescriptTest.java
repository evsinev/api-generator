package com.payneteasy.apigen;

import com.payneteasy.apigen.typescript.CreateTypescript;
import com.payneteasy.apigen.example.IExampleService;
import org.junit.Test;

public class CreateTypescriptTest {

    @Test
    public void test() {
        CreateTypescript createTypescript = new CreateTypescript("/api");
        String           typescript       = createTypescript.generateTypescript(IExampleService.class);
        System.out.println("typescript = \n" + typescript);
    }

}