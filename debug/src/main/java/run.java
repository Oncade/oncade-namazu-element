import dev.getelements.elements.sdk.local.ElementsLocalBuilder;

import java.io.IOException;

public class run {
    public static void main(final String[] args) throws IOException, InterruptedException {

        final var local = ElementsLocalBuilder.getDefault()
                .withSourceRoot()
                .withDeployment(builder -> builder
                        .useDefaultRepositories(true)
                        .elementPath()
                            .addSpiBuiltin("DEFAULT")
                            .addApiArtifact("zyx.oncade:api:1.0-SNAPSHOT")
                            .addElementArtifact("zyx.oncade:element:1.0-SNAPSHOT")
                        .endElementPath()
                        .build()
                )
                .build();

        local.start();
        local.run();

    }

}
