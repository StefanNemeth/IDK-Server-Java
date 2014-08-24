/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.catalog.CatalogItem;
import org.stevewinfield.suja.idk.game.catalog.CatalogPage;

public class CatalogPageWriter extends MessageWriter {

    public CatalogPageWriter(final CatalogPage page) {
        super(OperationCodes.getOutgoingOpCode("CatalogPage"));
        super.push(page.getId());
        super.push(page.getLayout());
        super.push(page.getLayoutStrings().length);

        for (final String layoutString : page.getLayoutStrings()) {
            super.push(layoutString);
        }

        super.push(page.getContentStrings().length);

        for (final String contentString : page.getContentStrings()) {
            super.push(contentString);
        }

        super.push(page.getItems().size()); // items

        for (final CatalogItem item : page.getItems().values()) {
            super.push(item);
        }

        super.push(-1);
    }

}
