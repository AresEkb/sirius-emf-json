/*******************************************************************************
 * Copyright (c) 2020, 2024 Obeo.
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
 * This exception is throw when a package prefix is not defined.
 *
 * @author <a href="mailto:guillaume.coutable@obeo.fr">Guillaume Coutable</a>
 */
public class PackagePrefixNotDefinedError implements Resource.Diagnostic {

    /**
     * The undefined namespace prefix.
     */
    protected final String prefix;

    /**
     * The source location of the issue.
     */
    private final String location;

    /**
     * The constructor.
     *
     * @param prefix
     *            the prefix
     * @param location
     *            the location
     */
    public PackagePrefixNotDefinedError(String prefix, String location) {
        this.prefix = Objects.requireNonNull(prefix);
        this.location = Objects.requireNonNull(location);
    }

    @Override
    public String getMessage() {
        return String.format("Package for prefix '%s' not defined.", this.prefix); //$NON-NLS-1$
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
     * Returns the prefix.
     *
     * @return The prefix
     */
    public String getPrefix() {
        return this.prefix;
    }

}
