package com.payneteasy.apigen.example;

import jakarta.ws.rs.Path;

@Path("/example")
public interface IExampleService {

    @Path("/get")
    ExampleResponse getExample(ExampleRequest aRequest);
}
