// Required annotation for an Element. Will recursively search folders
// from this point to include classes in the Element if recursive is true.
// Otherwise, you must include additional package-info.java files in child packages.
//@dev.getelements.elements.sdk.annotation.ElementDefinition(recursive = true)
@ElementDefinition(recursive = true)
// Enables DI via Guice
@GuiceElementModule(PongMultiModule.class)
// Allows injecting Mongo SDK from Elements Core
@ElementDependency("dev.getelements.elements.sdk.mongo")
// Allows injecting DAO layer from Elements Core
@ElementDependency("dev.getelements.elements.sdk.dao")
// Allows injecting Service layer from Elements Core
@ElementDependency("dev.getelements.elements.sdk.service")
// Allows injecting Utils layer from Elements Core
@ElementDependency("com.namazu.utils")

package com.namazu.pongmulti;

import com.namazu.pongmulti.guice.PongMultiModule;
import dev.getelements.elements.sdk.annotation.ElementDefinition;
import dev.getelements.elements.sdk.annotation.ElementDependency;
import dev.getelements.elements.sdk.spi.guice.annotations.GuiceElementModule;