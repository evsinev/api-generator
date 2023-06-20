package com.payneteasy.apigen.typescript;

import com.payneteasy.apigen.core.util.FindClassesInInterface;
import com.payneteasy.freemarker.FreemarkerFactory;
import com.payneteasy.freemarker.FreemarkerTemplate;

import java.io.File;

import static com.payneteasy.apigen.typescript.InterfaceMethodsToTypescript.getTypescriptMethods;

public class CreateTypescript {

    private final String prefixSegment;
    private final File   templateDir;

    public CreateTypescript(String prefixSegment, File templateDir) {
        this.prefixSegment = prefixSegment;
        this.templateDir   = templateDir;
    }

    public CreateTypescript(String prefixSegment) {
        this(prefixSegment, new File("no"));
    }

    private final JavaClassToTypescriptTypeConverter converter = new JavaClassToTypescriptTypeConverter();

    public String generateTypescript(Class<?> aInterface) {
        TypescriptMembers members = converter.getTopLevelTypes(FindClassesInInterface.getAllClassesForInterfaces(aInterface));

        return new FreemarkerFactory(templateDir)
                .template("services.ts")
                .instance()
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
