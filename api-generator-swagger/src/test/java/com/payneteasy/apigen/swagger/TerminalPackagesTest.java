package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.swagger.impl.TerminalPackages;
import com.payneteasy.apigen.swagger.task.ITaskServiceExternal;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TerminalPackagesTest {

    private static final String EXTERNAL_PACKAGE = "com.payneteasy.apigen.swagger.task.external.PretendFrameworkType";

    @Test
    public void terminalTypesAreNotIntrospected() {
        OpenAPI api = build(Collections.singletonList(EXTERNAL_PACKAGE));

        Set<String> schemas = api.getComponents().getSchemas().keySet();

        assertThat(schemas)
                .as("a terminal type must not become a component, nor drag its nested types in")
                .doesNotContain("PretendFrameworkType", "Nested");

        assertThat(schemas)
                .as("models that merely reference a terminal type are still described")
                .contains("ExternalHolder");
    }

    @Test
    public void typesOutsideTerminalPackagesAreStillIntrospected() {
        OpenAPI api = build(Collections.singletonList("org.example.nothing.matches."));

        assertThat(api.getComponents().getSchemas().keySet())
                .contains("ExternalHolder", "PretendFrameworkType");
    }

    @Test
    public void defaultsCoverThePackagesThatLeakedIntoRealDocuments() {
        assertThat(TerminalPackages.defaultPrefixes())
                .contains("sun.", "com.sun.", "jdk.", "org.springframework.", "org.joda.");
    }

    @Test
    public void standardLibraryTypesAreNotTerminal() {
        TerminalPackages defaults = TerminalPackages.defaults();

        // java.* must keep going through PrimitiveType, otherwise every String and every date
        // would be described as {type: object}
        assertThat(defaults.isTerminal(String.class)).isFalse();
        assertThat(defaults.isTerminal(java.util.Date.class)).isFalse();
        assertThat(defaults.isTerminal(Long.class)).isFalse();
    }

    private OpenAPI build(java.util.List<String> aTerminalPackages) {
        return SwaggerBuilder.builder()
                .openApi(new OpenAPI())
                .interfaces(Collections.singletonList(ITaskServiceExternal.class))
                .methodPathExtractor((aClass, aMethod) -> "/api/" + aClass.getSimpleName() + "." + aMethod.getName())
                .terminalPackages(aTerminalPackages)
                .build()
                .buildOpenApiModel();
    }
}
