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
package org.apache.isis.objectstore.jdo.datanucleus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Consumer;

import javax.jdo.PersistenceManagerFactory;

import org.apache.isis.core.runtime.system.context.IsisContext;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.PersistenceNucleusContext;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.enhancer.EnhancementHelper;
import org.datanucleus.store.AbstractStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Purges any state associated with DataNucleus.
 * 
 * @author ahuber@apache.org
 * @since 2.0.0
 *
 */
public class DataNucleusLifeCycleHelper {
	
    private static final Logger LOG = LoggerFactory.getLogger(DataNucleusLifeCycleHelper.class);

	public static void cleanUp(PersistenceManagerFactory persistenceManagerFactory) {
		
		try {
			
			final ClassLoader cl = IsisContext.getClassLoader();

//          XXX not needed according to https://github.com/datanucleus/datanucleus-core/issues/272 
//			
//			if(persistenceManagerFactory instanceof JDOPersistenceManagerFactory) {
//				
//				final JDOPersistenceManagerFactory jdoPMF = 
//						(JDOPersistenceManagerFactory) persistenceManagerFactory;
//				final PersistenceNucleusContext nucleusContext = jdoPMF.getNucleusContext();
//				final AbstractStoreManager storeManager = 
//						(AbstractStoreManager)nucleusContext.getStoreManager();
//				
//			
//				persistenceManagerFactory.getManagedClasses()
//				.forEach(clazz->{
//			        final ClassLoaderResolver clr = nucleusContext.getClassLoaderResolver(cl);
//	    		     	    		        
//			        // Un-manage from the store
//			        storeManager.unmanageClass(clr,	clazz.getName(), false);
//			        
//					 // Unload the meta-data for this class
//			        nucleusContext.getMetaDataManager().unloadMetaDataForClass(clazz.getName());
//				});
//			}
			
			persistenceManagerFactory.close();
			
			// XXX uses reflection prior to DN v5.1.5
			// remove once DN v5.1.5 is released
			// dnUnregisterClassesManagedBy(cl);
			
			// XXX for info, why we do this see
			// https://github.com/datanucleus/datanucleus-core/issues/272
			EnhancementHelper.getInstance().unregisterClasses(cl);
			
		} catch (Exception e) {
			// ignore, since it only affects re-deploy-ability, which is nice to have but not critical
		}

	}
    
    // -- HELPER
    
	// TODO remove once DN v5.1.5 is released
	private static void dnUnregisterClassesManagedBy(ClassLoader cl) {
    	if(cl==null)
    		return;
		visitDNRegisteredClasses(map->
			map.entrySet()
			.removeIf(entry->cl.equals(entry.getKey().getClassLoader()))
		);
	}
    
    // -- LOW LEVEL REFLECTION
    
	// TODO remove once DN v5.1.5 is released
	private final static MethodHandle getRegisteredClassesMH;
	static {
		MethodHandle mh;		
		try {
			Field registeredClasses = EnhancementHelper.class.getDeclaredField("registeredClasses");
			registeredClasses.setAccessible(true);
			mh = MethodHandles.lookup().unreflectGetter(registeredClasses);
			registeredClasses.setAccessible(false);
		} catch (Exception e) {
			mh = null;
			e.printStackTrace();
		}
		getRegisteredClassesMH = mh;
	}
	
	// TODO remove once DN v5.1.5 is released
	private static void visitDNRegisteredClasses(Consumer<Map<Class<?>, ?>> visitor){
		try {
			visitor.accept( (Map<Class<?>, ?>) getRegisteredClassesMH.invoke() );
		} catch (Throwable e) {
			LOG.warn("Failed to access DataNucleus' EnhancementHelper via reflection.", e);
		}
	}



}
