package zyx.oncade.element;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.Objects;

/**
 * Basic CORS filter that enables browser-based integrations to call the proxy API.
 */
@Provider
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    static final String DEFAULT_ALLOWED_HEADERS = "Authorization,Content-Type,X-Requested-With";
    static final String DEFAULT_ALLOWED_METHODS = String.join(",", HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Objects.requireNonNull(requestContext, "requestContext");
        if (HttpMethod.OPTIONS.equalsIgnoreCase(requestContext.getMethod())) {
            final String origin = resolveOrigin(requestContext);
            final Response.ResponseBuilder builder = Response.noContent()
                    .header("Access-Control-Allow-Origin", origin)
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Methods", resolveRequestedMethod(requestContext))
                    .header("Access-Control-Allow-Headers", resolveRequestedHeaders(requestContext))
                    .header("Access-Control-Max-Age", "86400");
            requestContext.abortWith(builder.build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Objects.requireNonNull(requestContext, "requestContext");
        Objects.requireNonNull(responseContext, "responseContext");

        final String origin = resolveOrigin(requestContext);
        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.putSingle("Access-Control-Allow-Origin", origin);
        headers.putSingle("Access-Control-Allow-Credentials", "true");
        if (!headers.containsKey("Access-Control-Allow-Methods")) {
            headers.putSingle("Access-Control-Allow-Methods", DEFAULT_ALLOWED_METHODS);
        }
        if (!headers.containsKey("Access-Control-Allow-Headers")) {
            headers.putSingle("Access-Control-Allow-Headers", DEFAULT_ALLOWED_HEADERS);
        }
        headers.add("Vary", "Origin");
    }

    private static String resolveOrigin(ContainerRequestContext requestContext) {
        final String origin = requestContext.getHeaderString("Origin");
        return origin == null || origin.isBlank() ? "*" : origin;
    }

    private static String resolveRequestedMethod(ContainerRequestContext requestContext) {
        final String requestedMethod = requestContext.getHeaderString("Access-Control-Request-Method");
        return requestedMethod == null || requestedMethod.isBlank() ? DEFAULT_ALLOWED_METHODS : requestedMethod;
    }

    private static String resolveRequestedHeaders(ContainerRequestContext requestContext) {
        final String requestedHeaders = requestContext.getHeaderString("Access-Control-Request-Headers");
        return requestedHeaders == null || requestedHeaders.isBlank() ? DEFAULT_ALLOWED_HEADERS : requestedHeaders;
    }
}
