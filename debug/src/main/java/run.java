import dev.getelements.elements.sdk.local.ElementsLocalBuilder;

import java.io.IOException;

public class run {
    public static void main(final String[] args) throws IOException, InterruptedException {

        final var local = ElementsLocalBuilder.getDefault()
                .withSourceRoot()
                .withDeployment(builder -> builder
                        .useDefaultRepositories(true)
                        .elementPackage()
                        .elmArtifact("zyx.oncade:element:elm:1.0.0")
                        .pathAttribute("zyx.oncade.element", "xyz.oncade.http.client.url", "http://host.docker.internal:3000/api/v1")
                        .pathAttribute("zyx.oncade.element", "xyz.oncade.http.client.apiKey", "oncade_live_5f1bd3677232a2208cef8a58bd19c4d1a5078a2a6fb774300ea05dc5997647a1")
                        .pathAttribute("zyx.oncade.element", "xyz.oncade.http.webhook.secret", "31c750289e2edd84ab8c6209b35d99ebc99b36ebbc23546ed446899c83a0f58b")
                        .endElementPackage()
                        .build()
                )
                .build();

        local.start();
        local.run();

    }

}
