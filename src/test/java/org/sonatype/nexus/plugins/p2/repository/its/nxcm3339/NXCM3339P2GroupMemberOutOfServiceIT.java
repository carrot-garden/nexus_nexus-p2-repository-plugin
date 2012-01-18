/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.plugins.p2.repository.its.nxcm3339;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers.exists;
import static org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers.isDirectory;

import java.io.File;

import org.restlet.data.MediaType;
import org.sonatype.nexus.plugins.p2.repository.its.AbstractNexusProxyP2IT;
import org.sonatype.nexus.test.utils.RepositoryMessageUtil;
import org.testng.annotations.Test;

public class NXCM3339P2GroupMemberOutOfServiceIT
    extends AbstractNexusProxyP2IT
{

    private RepositoryMessageUtil repositoryMessageUtil;

    public NXCM3339P2GroupMemberOutOfServiceIT()
    {
        super( "nxcm3339" );
        repositoryMessageUtil = new RepositoryMessageUtil( this, getJsonXStream(), MediaType.APPLICATION_JSON );
    }

    /**
     * When one of member repositories is out of service the group repository should not fail and just use the valid
     * repositories.
     *
     * @throws Exception not expected
     */
    @Test
    public void outOfService()
        throws Exception
    {
        repositoryMessageUtil.setOutOfServiceProxy( "nxcm3339-2", true );

        final File installDir = new File( "target/eclipse/nxcm3339" );

        installUsingP2(
            getGroupUrl( getTestRepositoryId() ),
            "com.sonatype.nexus.p2.its.feature.feature.group",
            installDir.getCanonicalPath()
        );

        final File feature = new File( installDir, "features/com.sonatype.nexus.p2.its.feature_1.0.0" );
        assertThat( feature, exists() );
        assertThat( feature, isDirectory() );
    }

}