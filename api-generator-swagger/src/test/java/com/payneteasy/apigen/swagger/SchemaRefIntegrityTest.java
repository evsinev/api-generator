package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.swagger.task.ITaskService;
import com.payneteasy.apigen.swagger.task.ITaskServiceBugs;
import com.payneteasy.apigen.swagger.task.ITaskServiceRefs;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Every {@code $ref} in the produced document must resolve against {@code components/schemas}.
 * A document that breaks this renders as an error in strict viewers (Redoc) and silently loses
 * fields in lenient ones (Scalar), and neither javac nor the other tests notice.
 */
public class SchemaRefIntegrityTest {

    @Test
    public void taskServiceHasNoDanglingRefs() {
        assertNoDanglingRefs(ITaskService.class);
    }

    @Test
    public void taskServiceBugsHasNoDanglingRefs() {
        assertNoDanglingRefs(ITaskServiceBugs.class);
    }

    @Test
    public void computedAndWrittenPropertiesHaveNoDanglingRefs() {
        assertNoDanglingRefs(ITaskServiceRefs.class);
    }

    private void assertNoDanglingRefs(Class<?> aServiceInterface) {
        OpenAPI api = SwaggerBuilder.builder()
                .openApi(new OpenAPI())
                .methodAcceptor(SchemaRefIntegrityTest::acceptMethod)
                .interfaces(Collections.singletonList(aServiceInterface))
                .methodPathExtractor((aClass, aMethod) -> "/api/" + aClass.getSimpleName() + "." + aMethod.getName())
                .build()
                .buildOpenApiModel();

        Set<String> referenced = collectRefNames(api);
        Set<String> defined    = api.getComponents() == null || api.getComponents().getSchemas() == null
                ? Collections.emptySet()
                : api.getComponents().getSchemas().keySet();

        Set<String> dangling = new TreeSet<>(referenced);
        dangling.removeAll(defined);

        assertThat(dangling)
                .as("$ref without a schema in %s; defined schemas are %s",
                        aServiceInterface.getSimpleName(), new TreeSet<>(defined))
                .isEmpty();
    }

    private static Set<String> collectRefNames(OpenAPI aApi) {
        Set<String> names = new TreeSet<>();
        collectRefNames(aApi, names);
        return names;
    }

    private static void collectRefNames(Object aNode, Set<String> aNames) {
        if (aNode instanceof Schema) {
            Schema<?> schema = (Schema<?>) aNode;
            if (schema.get$ref() != null) {
                aNames.add(refName(schema.get$ref()));
            }
            collectRefNames(schema.getItems(), aNames);
            collectRefNames(schema.getProperties(), aNames);
            collectRefNames(schema.getAdditionalProperties(), aNames);
            collectRefNames(schema.getAllOf(), aNames);
            collectRefNames(schema.getAnyOf(), aNames);
            collectRefNames(schema.getOneOf(), aNames);
            return;
        }
        if (aNode instanceof Map) {
            collectRefNames(((Map<?, ?>) aNode).values(), aNames);
            return;
        }
        if (aNode instanceof Collection) {
            for (Object item : (Collection<?>) aNode) {
                collectRefNames(item, aNames);
            }
            return;
        }
        if (aNode instanceof OpenAPI) {
            OpenAPI api = (OpenAPI) aNode;
            collectRefNames(api.getPaths(), aNames);
            if (api.getComponents() != null) {
                collectRefNames(api.getComponents().getSchemas(), aNames);
            }
            return;
        }
        // Paths / PathItem / Operation / RequestBody / ApiResponse / MediaType are plain beans:
        // walking them generically would need reflection, so the few we care about are handled
        // through the Paths map below.
        if (aNode instanceof io.swagger.v3.oas.models.Paths) {
            collectRefNames(((Map<?, ?>) aNode).values(), aNames);
            return;
        }
        if (aNode instanceof io.swagger.v3.oas.models.PathItem) {
            io.swagger.v3.oas.models.PathItem item = (io.swagger.v3.oas.models.PathItem) aNode;
            collectRefNames(item.readOperations(), aNames);
            return;
        }
        if (aNode instanceof io.swagger.v3.oas.models.Operation) {
            io.swagger.v3.oas.models.Operation operation = (io.swagger.v3.oas.models.Operation) aNode;
            collectRefNames(operation.getRequestBody(), aNames);
            collectRefNames(operation.getResponses(), aNames);
            collectRefNames(operation.getParameters(), aNames);
            return;
        }
        if (aNode instanceof io.swagger.v3.oas.models.parameters.RequestBody) {
            collectRefNames(((io.swagger.v3.oas.models.parameters.RequestBody) aNode).getContent(), aNames);
            return;
        }
        if (aNode instanceof io.swagger.v3.oas.models.parameters.Parameter) {
            collectRefNames(((io.swagger.v3.oas.models.parameters.Parameter) aNode).getSchema(), aNames);
            return;
        }
        if (aNode instanceof io.swagger.v3.oas.models.responses.ApiResponse) {
            collectRefNames(((io.swagger.v3.oas.models.responses.ApiResponse) aNode).getContent(), aNames);
            return;
        }
        if (aNode instanceof io.swagger.v3.oas.models.media.MediaType) {
            collectRefNames(((io.swagger.v3.oas.models.media.MediaType) aNode).getSchema(), aNames);
        }
    }

    private static String refName(String aRef) {
        int slash = aRef.lastIndexOf('/');
        return slash < 0 ? aRef : aRef.substring(slash + 1);
    }

    private static boolean acceptMethod(Class<?> aClass, Method aMethod) {
        for (Class<?> type : aMethod.getParameterTypes()) {
            if (InputStream.class.isAssignableFrom(type)) {
                return false;
            }
        }
        return true;
    }
}
