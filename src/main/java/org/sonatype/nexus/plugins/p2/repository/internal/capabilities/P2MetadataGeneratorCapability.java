/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.internal.capabilities;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sonatype.nexus.plugins.p2.repository.internal.capabilities.P2MetadataGeneratorCapabilityDescriptor.TYPE_ID;

import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.plugins.capabilities.Condition;
import org.sonatype.nexus.plugins.capabilities.support.CapabilitySupport;
import org.sonatype.nexus.plugins.capabilities.support.condition.Conditions;
import org.sonatype.nexus.plugins.capabilities.support.condition.RepositoryConditions;
import org.sonatype.nexus.plugins.p2.repository.P2MetadataGenerator;
import org.sonatype.nexus.plugins.p2.repository.P2MetadataGeneratorConfiguration;
import org.sonatype.nexus.proxy.NoSuchRepositoryException;
import org.sonatype.nexus.proxy.registry.RepositoryRegistry;

@Named( TYPE_ID )
public class P2MetadataGeneratorCapability
    extends CapabilitySupport
{

    private final P2MetadataGenerator service;

    private final Conditions conditions;

    private final RepositoryRegistry repositoryRegistry;

    private P2MetadataGeneratorConfiguration configuration;

    @Inject
    public P2MetadataGeneratorCapability( final P2MetadataGenerator service,
                                          final Conditions conditions,
                                          final RepositoryRegistry repositoryRegistry )
    {
        this.service = checkNotNull( service );
        this.conditions = checkNotNull( conditions );
        this.repositoryRegistry = checkNotNull( repositoryRegistry );
    }

    @Override
    public String description()
    {
        if ( configuration != null )
        {
            try
            {
                return repositoryRegistry.getRepository( configuration.repositoryId() ).getName();
            }
            catch ( NoSuchRepositoryException e )
            {
                return configuration.repositoryId();
            }
        }
        return null;
    }

    @Override
    public void onCreate()
        throws Exception
    {
        configuration = createConfiguration( context().properties() );
    }

    @Override
    public void onLoad()
        throws Exception
    {
        configuration = createConfiguration( context().properties() );
    }

    @Override
    public void onUpdate()
        throws Exception
    {
        configuration = createConfiguration( context().properties() );
    }

    @Override
    public void onRemove()
        throws Exception
    {
        configuration = null;
    }

    @Override
    public void onActivate()
    {
        service.addConfiguration( configuration );
    }

    @Override
    public void onPassivate()
    {
        service.removeConfiguration( configuration );
    }

    @Override
    public Condition activationCondition()
    {
        return conditions.logical().and(
            conditions.repository().repositoryIsInService( new RepositoryConditions.RepositoryId()
            {
                @Override
                public String get()
                {
                    return configuration != null ? configuration.repositoryId() : null;
                }
            } ),
            conditions.capabilities().passivateCapabilityDuringUpdate()
        );
    }

    @Override
    public Condition validityCondition()
    {
        return conditions.repository().repositoryExists( new RepositoryConditions.RepositoryId()
        {
            @Override
            public String get()
            {
                return configuration != null ? configuration.repositoryId() : null;
            }
        } );
    }

    private P2MetadataGeneratorConfiguration createConfiguration( final Map<String, String> properties )
    {
        return new P2MetadataGeneratorConfiguration( properties );
    }

}
