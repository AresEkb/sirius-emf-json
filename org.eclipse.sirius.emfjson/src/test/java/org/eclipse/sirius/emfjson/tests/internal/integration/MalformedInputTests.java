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
package org.eclipse.sirius.emfjson.tests.internal.integration;

import static org.junit.Assert.assertEquals;

import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.junit.Test;

/**
 * Tests that malformed input is reported the same way whether the resource is
 * read by the streaming reader or by the tree-based one.
 *
 * <p>
 * Which one runs is an internal decision - streaming is used unless a resource
 * processor is installed - so it must not change what the caller observes. The
 * two readers fail differently on their own: Gson turns malformed input into a
 * {@code JsonSyntaxException}, while the streaming reader surfaces the raw
 * parser failure.
 * </p>
 *
 * @author <a href="mailto:denis.nikif@gmail.com">Denis Nikiforov</a>
 */
public class MalformedInputTests {

    /**
     * A processor that does nothing but is not the {@code NoOp} one, which is
     * what makes the resource take the tree-based branch.
     *
     * @author <a href="mailto:denis.nikif@gmail.com">Denis Nikiforov</a>
     */
    private static final class TreeForcingProcessor implements JsonResource.IJsonResourceProcessor {

        @Override
        public void preDeserialization(JsonResource resource, JsonObject jsonObject) {
            // Do nothing
        }

        @Override
        public void postSerialization(JsonResource resource, JsonObject jsonObject) {
            // Do nothing
        }

        @Override
        public Object getValue(JsonResource resource, EObject eObject, EStructuralFeature feature, Object value) {
            return null;
        }

        @Override
        public void postObjectLoading(JsonResource resource, EObject eObject, JsonObject jsonObject, boolean isTopObject) {
            // Do nothing
        }
    }

    /**
     * Loads truncated content and returns the class of whatever it threw, or
     * {@code null} when the load completed.
     *
     * @param options
     *            the resource options
     * @return the class of the thrown exception, or {@code null}
     */
    private Class<?> loadTruncatedContent(Map<Object, Object> options) {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

        Resource resource = new JsonResourceImpl(URI.createURI("sirius:///resource"), options); //$NON-NLS-1$
        resourceSet.getResources().add(resource);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream("{".getBytes())) { //$NON-NLS-1$
            resource.load(inputStream, null);
        } catch (IOException | RuntimeException exception) {
            return exception.getClass();
        }
        return null;
    }

    /**
     * Tests that both readers report truncated content the same way.
     */
    @Test
    public void testBothReadersReportTruncatedContentAlike() {
        Map<Object, Object> streaming = new HashMap<>();
        streaming.put(JsonResource.OPTION_STREAMING_LOAD, Boolean.TRUE);

        // Same option, but the processor sends the resource down the tree-based branch.
        Map<Object, Object> tree = new HashMap<>();
        tree.put(JsonResource.OPTION_STREAMING_LOAD, Boolean.TRUE);
        tree.put(JsonResource.OPTION_JSON_RESSOURCE_PROCESSOR, new TreeForcingProcessor());

        assertEquals(this.loadTruncatedContent(tree), this.loadTruncatedContent(streaming));
    }

}
