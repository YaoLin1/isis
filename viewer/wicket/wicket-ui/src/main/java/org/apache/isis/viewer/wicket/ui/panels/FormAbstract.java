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

package org.apache.isis.viewer.wicket.ui.panels;

import java.util.List;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.oid.stringable.OidStringifier;
import org.apache.isis.runtimes.dflt.runtime.system.context.IsisContext;
import org.apache.isis.runtimes.dflt.runtime.system.persistence.PersistenceSession;
import org.apache.isis.viewer.wicket.model.nof.AuthenticationSessionAccessor;
import org.apache.isis.viewer.wicket.model.nof.PersistenceSessionAccessor;
import org.apache.isis.viewer.wicket.ui.app.registry.ComponentFactoryRegistry;
import org.apache.isis.viewer.wicket.ui.app.registry.ComponentFactoryRegistryAccessor;
import org.apache.isis.viewer.wicket.ui.pages.PageClassRegistry;
import org.apache.isis.viewer.wicket.ui.pages.PageClassRegistryAccessor;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public abstract class FormAbstract<T> extends Form<T> implements IHeaderContributor, ComponentFactoryRegistryAccessor, PageClassRegistryAccessor, AuthenticationSessionAccessor, PersistenceSessionAccessor {

    private static final long serialVersionUID = 1L;

    public FormAbstract(final String id) {
        super(id);
    }

    public FormAbstract(final String id, final IModel<T> model) {
        super(id, model);
    }

    // ///////////////////////////////////////////////////////////////////
    // IHeaderContributor
    // ///////////////////////////////////////////////////////////////////

    /**
     * Automatically reference any corresponding CSS.
     */
    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        renderHead(response, this.getClass());
    }

    /**
     * Factored out to allow non-concrete subclasses to additionally render
     * their own CSS if required.
     */
    protected void renderHead(final IHeaderResponse response, final Class<?> cls) {
        final String url = cls.getSimpleName() + ".css";
        response.renderCSSReference(new ResourceReference(cls, url));
    }

    // ///////////////////////////////////////////////////////////////////
    // Convenience
    // ///////////////////////////////////////////////////////////////////

    @Override
    public ComponentFactoryRegistry getComponentFactoryRegistry() {
        return ((ComponentFactoryRegistryAccessor) getApplication()).getComponentFactoryRegistry();
    }

    @Override
    public PageClassRegistry getPageClassRegistry() {
        return ((PageClassRegistryAccessor) getApplication()).getPageClassRegistry();
    }

    // ///////////////////////////////////////////////////////////////////
    // Dependencies (from IsisContext)
    // ///////////////////////////////////////////////////////////////////

    public IsisContext getIsisContext() {
        return IsisContext.getInstance();
    }

    @Override
    public PersistenceSession getPersistenceSession() {
        return IsisContext.getPersistenceSession();
    }

    @Override
    public AuthenticationSession getAuthenticationSession() {
        return IsisContext.getAuthenticationSession();
    }

    protected List<ObjectAdapter> getServiceAdapters() {
        return getPersistenceSession().getServices();
    }

    protected OidStringifier getOidStringifier() {
        return getPersistenceSession().getOidGenerator().getOidStringifier();
    }

}
