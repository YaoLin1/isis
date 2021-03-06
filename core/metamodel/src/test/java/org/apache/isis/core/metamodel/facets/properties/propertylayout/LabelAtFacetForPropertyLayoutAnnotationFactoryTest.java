/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.core.metamodel.facets.properties.propertylayout;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.isis.applib.annotation.LabelPosition;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.isis.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.isis.core.metamodel.facets.objectvalue.labelat.LabelAtFacet;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class LabelAtFacetForPropertyLayoutAnnotationFactoryTest extends AbstractFacetFactoryTest {

    public void testPropertyLayoutAnnotationPickedUp() {
        final PropertyLayoutFacetFactory facetFactory = new PropertyLayoutFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @PropertyLayout(labelPosition = LabelPosition.LEFT)
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        final ProcessMethodContext processMethodContext =
                new ProcessMethodContext(Customer.class, null, method, methodRemover, facetedMethod);

        // when
        final FacetHolder holder = facetFactory.facetHolderFrom(processMethodContext);
        final List<PropertyLayout> propertyLayouts = facetFactory.propertyLayoutsFrom(processMethodContext);

        facetFactory.processLabelAt(holder, propertyLayouts);

        // then
        final Facet facet = facetedMethod.getFacet(LabelAtFacet.class);
        assertThat(facet, is(notNullValue()));
        assertThat(facet, is(instanceOf(LabelAtFacetForPropertyLayoutAnnotation.class)));
        final LabelAtFacetForPropertyLayoutAnnotation layoutAnnotation = (LabelAtFacetForPropertyLayoutAnnotation) facet;
        assertThat(layoutAnnotation.label(), is(equalTo(LabelPosition.LEFT)));
    }
}
