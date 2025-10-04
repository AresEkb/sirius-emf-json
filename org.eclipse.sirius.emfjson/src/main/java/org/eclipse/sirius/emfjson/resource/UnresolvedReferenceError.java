/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.emfjson.resource;

import java.util.Objects;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * This exception is throw when a reference is unresolved.
 *
 * @author denis
 */
public class UnresolvedReferenceError implements Resource.Diagnostic {

    /**
     * The unresolved reference.
     */
    protected final String reference;

    /**
     * The source location of the issue.
     */
    private final String location;

    /**
     * The constructor.
     *
     * @param uri
     *            the URI
     * @param location
     *            the location
     */
    public UnresolvedReferenceError(String uri, String location) {
        this.reference = Objects.requireNonNull(uri);
        this.location = Objects.requireNonNull(location);
    }

    @Override
    public String getMessage() {
        return String.format("The reference '%s' not resolved.", this.reference); //$NON-NLS-1$
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public int getColumn() {
        return 0;
    }

    @Override
    public int getLine() {
        return 0;
    }

    /**
     * Returns the reference.
     *
     * @return The reference
     */
    public String getReference() {
        return this.reference;
    }

}
