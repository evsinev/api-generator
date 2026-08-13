package com.payneteasy.apigen.swagger;

import com.payneteasy.apigen.swagger.task.ITaskServiceGenericRequests;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A request that is a collection rather than a request object. Such a method is only describable
 * together with the element type of that collection, and passing the erased class on alone used to
 * cost it its path: the failure is logged per method and the rest of the document is built as if
 * nothing were missing.
 *
 * @see com.payneteasy.apigen.swagger.impl.CollectionGenericTypeTest for the same argument shapes read
 *      off a field
 */
public class CollectionGenericsTest {

    @Test
    public void listOfModelsCarriesItsItemType() {
        Schema<?> body = requestSchema("/api/listOfItems");

        assertThat(body.getType()).isEqualTo("array");
        assertThat(body.getItems().get$ref()).isEqualTo("#/components/schemas/TaskItem");
    }

    /** A wrapper type is described inline rather than by $ref. */
    @Test
    public void listOfWrappersCarriesItsItemType() {
        Schema<?> body = requestSchema("/api/listOfLongs");

        assertThat(body.getType()).isEqualTo("array");
        assertThat(body.getItems().get$ref()).isNull();
        assertThat(body.getItems().getType()).isEqualTo("integer");
    }

    private static Schema<?> requestSchema(String aPath) {
        OpenAPI api = SwaggerBuilder.builder()
                .openApi(new OpenAPI())
                .methodAcceptor((aClass, aMethod) -> true)
                .interfaces(Collections.singletonList(ITaskServiceGenericRequests.class))
                .methodPathExtractor((aClass, aMethod) -> "/api/" + aMethod.getName())
                .build()
                .buildOpenApiModel();

        PathItem path = api.getPaths().get(aPath);
        assertThat(path).as("no path %s; the document has %s", aPath, api.getPaths().keySet()).isNotNull();

        Operation operation = path.readOperations().get(0);
        return operation.getRequestBody()
                .getContent()
                .get("application/json")
                .getSchema();
    }
}
