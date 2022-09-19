package com.payneteasy.apigen.core.typescript.example;

import jakarta.ws.rs.Path;

@Path("/example")
public interface IExampleService {

    @Path("/get")
    ExampleResponse getExample(ExampleRequest aRequest);
}
