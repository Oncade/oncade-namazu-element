// Required annotation for an Element. Will recursively search folders
// from this point to include classes in the Element if recursive is true.
// Otherwise, you must include additional package-info.java files in child packages.
//@dev.getelements.elements.sdk.annotation.ElementDefinition(recursive = true)
@ElementDefinition(recursive = true)
@GuiceElementModule(OncadeElementModule.class)
@ElementDependency("dev.getelements.elements.sdk.dao")
@ElementDependency("dev.getelements.elements.sdk.mongo")
@ElementDependency("dev.getelements.elements.sdk.service")
@ElementService(
        value = OncadeReceiptEventHandler.class,
        implementation = @ElementServiceImplementation(OncadeReceiptEventHandlerImpl.class)
)
package zyx.oncade.element;

import dev.getelements.elements.sdk.annotation.ElementDefinition;
import dev.getelements.elements.sdk.annotation.ElementDependency;
import dev.getelements.elements.sdk.annotation.ElementService;
import dev.getelements.elements.sdk.annotation.ElementServiceImplementation;
import dev.getelements.elements.sdk.spi.guice.annotations.GuiceElementModule;
import zyx.oncade.element.guice.OncadeElementModule;
import zyx.oncade.element.service.receipt.OncadeReceiptEventHandler;
import zyx.oncade.element.service.receipt.OncadeReceiptEventHandlerImpl;
