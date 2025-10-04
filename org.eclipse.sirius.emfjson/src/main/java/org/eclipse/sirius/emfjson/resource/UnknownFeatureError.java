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
 * This exception is throw when a feature is not found for a class.
 *
 * @author denis
 */
public class UnknownFeatureError implements Resource.Diagnostic {

    /**
     * The class name.
     */
    protected final String className;

    /**
     * The feature name.
     */
    protected final String featureName;

    /**
     * The source location of the issue.
     */
    private final String location;

    /**
     * The constructor.
     *
     * @param className
     *            the class name
     * @param featureName
     *            the feature name
     * @param location
     *            the location
     */
    public UnknownFeatureError(String className, String featureName, String location) {
        this.className = Objects.requireNonNull(className);
        this.featureName = Objects.requireNonNull(featureName);
        this.location = Objects.requireNonNull(location);
    }

    @Override
    public String getMessage() {
        return String.format("Feature '%s' not found for class '%s'.", this.featureName, this.className); //$NON-NLS-1$
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
     * Returns the class name.
     *
     * @return The class name
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Returns the feature name.
     *
     * @return The feature name
     */
    public String getFeatureName() {
        return this.featureName;
    }

}
