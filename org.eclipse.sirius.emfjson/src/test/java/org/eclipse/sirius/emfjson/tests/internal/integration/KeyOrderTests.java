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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.eclipse.sirius.emfjson.utils.GsonEObjectDeserializer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests that an object is deserialized whatever the order of its keys.
 *
 * <p>
 * Deserialization streams the data of an object as soon as its class is known,
 * which assumes {@code eClass} comes first. Writers are under no such
 * obligation, and a document round-tripped through a store that normalises the
 * keys - PostgreSQL {@code jsonb} orders them by length, which puts
 * {@code data} first - arrives the other way round.
 * </p>
 *
 * @author <a href="mailto:denis.nikif@gmail.com">Denis Nikiforov</a>
 */
public class KeyOrderTests {

    /**
     * Returns options that turn the streaming reader on, since these tests exist
     * to pin down how it copes with the order of the keys.
     *
     * @return the resource options
     */
    private static Map<Object, Object> streamingOptions() {
        Map<Object, Object> options = new HashMap<>();
        options.put(JsonResource.OPTION_STREAMING_LOAD, Boolean.TRUE);
        return options;
    }

    /**
     * Loads a one-object resource and returns the name of its root EClass.
     *
     * @param json
     *            the serialized resource
     * @return the name of the root EClass
     */
    private String loadRootClassName(String json) {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

        Resource resource = new JsonResourceImpl(URI.createURI("sirius:///resource"), streamingOptions()); //$NON-NLS-1$
        resourceSet.getResources().add(resource);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes())) {
            resource.load(inputStream, null);
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
        }

        assertTrue(resource.getErrors().toString(), resource.getErrors().isEmpty());
        assertEquals(1, resource.getContents().size());
        return ((EClass) resource.getContents().get(0)).getName();
    }

    /**
     * Tests that the data is deserialized when eClass comes first.
     */
    @Test
    public void testEClassBeforeData() {
        String json = "{\"json\":{\"version\":\"1.0\"}," //$NON-NLS-1$
                + "\"ns\":{\"ecore\":\"" + EcorePackage.eNS_URI + "\"}," //$NON-NLS-1$ //$NON-NLS-2$
                + "\"content\":[{\"eClass\":\"ecore:EClass\",\"data\":{\"name\":\"Entity\"}}]}"; //$NON-NLS-1$
        assertEquals("Entity", this.loadRootClassName(json)); //$NON-NLS-1$
    }

    /**
     * Tests that the data is deserialized when it comes before eClass.
     */
    @Test
    public void testDataBeforeEClass() {
        String json = "{\"json\":{\"version\":\"1.0\"}," //$NON-NLS-1$
                + "\"ns\":{\"ecore\":\"" + EcorePackage.eNS_URI + "\"}," //$NON-NLS-1$ //$NON-NLS-2$
                + "\"content\":[{\"data\":{\"name\":\"Entity\"},\"eClass\":\"ecore:EClass\"}]}"; //$NON-NLS-1$
        assertEquals("Entity", this.loadRootClassName(json)); //$NON-NLS-1$
    }

    /**
     * Tests that a contained object is deserialized when its data comes first.
     */
    @Test
    public void testDataBeforeEClassOnContainedObject() {
        String json = "{\"json\":{\"version\":\"1.0\"}," //$NON-NLS-1$
                + "\"ns\":{\"ecore\":\"" + EcorePackage.eNS_URI + "\"}," //$NON-NLS-1$ //$NON-NLS-2$
                + "\"content\":[{\"data\":{\"name\":\"pkg\",\"eClassifiers\":[" //$NON-NLS-1$
                + "{\"data\":{\"name\":\"Entity\"},\"eClass\":\"ecore:EClass\"}]}," //$NON-NLS-1$
                + "\"eClass\":\"ecore:EPackage\"}]}"; //$NON-NLS-1$

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        Resource resource = new JsonResourceImpl(URI.createURI("sirius:///resource"), streamingOptions()); //$NON-NLS-1$
        resourceSet.getResources().add(resource);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes())) {
            resource.load(inputStream, null);
        } catch (IOException exception) {
            Assert.fail(exception.getMessage());
        }

        assertTrue(resource.getErrors().toString(), resource.getErrors().isEmpty());
        EPackage ePackage = (EPackage) resource.getContents().get(0);
        assertEquals("pkg", ePackage.getName()); //$NON-NLS-1$
        assertEquals(1, ePackage.getEClassifiers().size());
        assertEquals("Entity", ePackage.getEClassifiers().get(0).getName()); //$NON-NLS-1$
    }

    /**
     * Tests that the reader is tree-based unless the streaming option is set,
     * which is what makes the order of the keys a non-issue by default.
     */
    @Test
    public void testStreamingIsOffByDefault() {
        JsonResource resource = new JsonResourceImpl(URI.createURI("sirius:///resource"), new HashMap<>()); //$NON-NLS-1$
        new ResourceSetImpl().getResources().add(resource);

        assertFalse(new GsonEObjectDeserializer(resource, new HashMap<>()).canStream());
        assertTrue(new GsonEObjectDeserializer(resource, streamingOptions()).canStream());
    }

}
