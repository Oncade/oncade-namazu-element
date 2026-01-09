package com.oncade.pong.presale;

import com.oncade.pong.presale.rest.PresaleProxyResource;
import dev.getelements.elements.sdk.annotation.ElementServiceExport;
import dev.getelements.elements.sdk.annotation.ElementServiceImplementation;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.core.Application;

import java.util.Set;

@ElementServiceImplementation
@ElementServiceExport(Application.class)
public class PresaleProxyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                PresaleProxyResource.class,
                OpenApiResource.class
        );
    }
}
