/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.actions.events;

import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;

/**
 * A wrapper for a {@link AbstractITextProductEvent} storing additional meta data about the event.
 * If wrapped event is immutable then the instance of the wrapper is immutable too.
 */
public class ITextProductEventWrapper {
    private final AbstractITextProductEvent event;
    private final String productUsageType;
    private final String producerLine;

    /**
     * Creates a wrapper for the event.
     *
     * @param event is a {@link AbstractITextProductEvent} to wrap
     * @param productUsageType is a product usage marker
     * @param producerLine is a producer line defined by the {@link ITextProductEventProcessor}
     *                     which registered the event
     */
    public ITextProductEventWrapper(AbstractITextProductEvent event, String productUsageType, String producerLine) {
        this.event = event;
        this.productUsageType = productUsageType;
        this.producerLine = producerLine;
    }

    /**
     * Obtains the wrapped event.
     *
     * @return wrapped event
     */
    public AbstractITextProductEvent getEvent() {
        return event;
    }

    /**
     * Obtains the license type for the product which generated the event.
     *
     * @return product usage type
     */
    public String getProductUsageType() {
        return productUsageType;
    }

    /**
     * Gets producer line defined by the {@link ITextProductEventProcessor} which registered the
     * event.
     *
     * @return producer line
     */
    public String getProducerLine() {
        return producerLine;
    }
}