package com.payneteasy.apigen.core.typescript;

import com.payneteasy.apigen.core.util.FindClassesInInterface;
import com.payneteasy.freemarker.FreemarkerFactory;
import com.payneteasy.freemarker.FreemarkerTemplate;

import java.io.File;

import static com.payneteasy.apigen.core.typescript.InterfaceMethodsToTypescript.getTypescriptMethods;

public class CreateTypescript {

    private final String prefixSegment;

    public CreateTypescript(String prefixSegment) {
        this.prefixSegment = prefixSegment;
    }

    private final FreemarkerTemplate template = new FreemarkerFactory(new File("no"))
            .template("services.ts");

    private final JavaClassToTypescriptTypeConverter converter = new JavaClassToTypescriptTypeConverter();

    public String generateTypescript(Class<?> aInterface) {
        TypescriptMembers members = converter.getTopLevelTypes(FindClassesInInterface.getAllClassesForInterfaces(aInterface));

        return template.instance()
                .add("types", members.getTypes())
                .add("enums", members.getEnums())
                .add("methods", getTypescriptMethods(prefixSegment, aInterface))
                .add("className", remoteFirstLetter(aInterface.getSimpleName()))
                .createText();

    }

    private String remoteFirstLetter(String aName) {
        return aName.startsWith("I") ? aName.substring(1) : aName;
    }

}
