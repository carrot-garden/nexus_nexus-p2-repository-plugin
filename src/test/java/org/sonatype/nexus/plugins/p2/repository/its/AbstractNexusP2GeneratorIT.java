package org.sonatype.nexus.plugins.p2.repository.its;

import static org.sonatype.nexus.plugins.p2.repository.P2Constants.ARTIFACTS_XML;
import static org.sonatype.nexus.plugins.p2.repository.P2Constants.P2_REPOSITORY_ROOT_PATH;

import java.io.File;
import java.io.IOException;

import org.sonatype.nexus.plugins.capabilities.internal.rest.dto.CapabilityPropertyResource;
import org.sonatype.nexus.plugins.capabilities.internal.rest.dto.CapabilityResource;
import org.sonatype.nexus.plugins.p2.repository.P2Constants;
import org.sonatype.nexus.plugins.p2.repository.P2MetadataGenerator;
import org.sonatype.nexus.plugins.p2.repository.P2MetadataGeneratorConfiguration;
import org.sonatype.nexus.plugins.p2.repository.P2RepositoryGenerator;
import org.sonatype.nexus.plugins.p2.repository.P2RepositoryGeneratorConfiguration;
import org.sonatype.nexus.plugins.p2.repository.internal.capabilities.P2MetadataGeneratorCapability;
import org.sonatype.nexus.plugins.p2.repository.internal.capabilities.P2RepositoryGeneratorCapability;
import org.sonatype.nexus.test.utils.CapabilitiesMessageUtil;

public abstract class AbstractNexusP2GeneratorIT
    extends AbstractNexusP2IT
{

    private String p2RepositoryGeneratorCapabilityId;

    public AbstractNexusP2GeneratorIT( final String repoId )
    {
        super( repoId );
    }

    protected void createP2MetadataGeneratorCapability()
        throws Exception
    {
        final CapabilityResource capability = new CapabilityResource();
        capability.setName( P2MetadataGenerator.class.getName() );
        capability.setTypeId( P2MetadataGeneratorCapability.ID );

        final CapabilityPropertyResource repoProp = new CapabilityPropertyResource();
        repoProp.setKey( P2MetadataGeneratorConfiguration.REPO_OR_GROUP_ID );
        repoProp.setValue( getTestRepositoryId() );

        capability.addProperty( repoProp );

        CapabilitiesMessageUtil.create( capability );
    }

    protected void createP2RepositoryGeneratorCapability()
        throws Exception
    {
        final CapabilityResource capability = new CapabilityResource();
        capability.setName( P2RepositoryGenerator.class.getName() );
        capability.setTypeId( P2RepositoryGeneratorCapability.ID );

        final CapabilityPropertyResource repoProp = new CapabilityPropertyResource();
        repoProp.setKey( P2RepositoryGeneratorConfiguration.REPO_OR_GROUP_ID );
        repoProp.setValue( getTestRepositoryId() );

        capability.addProperty( repoProp );

        p2RepositoryGeneratorCapabilityId = CapabilitiesMessageUtil.create( capability ).getId();
    }

    protected void removeP2RepositoryGeneratorCapability()
        throws Exception
    {
        CapabilitiesMessageUtil.delete( p2RepositoryGeneratorCapabilityId );
    }

    protected void deployArtifact( final String repoId, final File fileToDeploy, final String path )
        throws Exception
    {
        final String deployUrl = getNexusTestRepoUrl( repoId );
        final String deployUrlProtocol = deployUrl.substring( 0, deployUrl.indexOf( ":" ) );
        final String wagonHint = getWagonHintForDeployProtocol( deployUrlProtocol );
        getDeployUtils().deployWithWagon( wagonHint, deployUrl, fileToDeploy, path );
    }

    protected File downloadP2ArtifactsFor( final String groupId, final String artifactId, final String version )
        throws IOException
    {
        final File downloadDir = new File( "target/downloads/" + this.getClass().getSimpleName() );
        final File p2Artifacts =
            downloadArtifact( groupId, artifactId, version, "xml", "p2Artifacts", downloadDir.getCanonicalPath() );
        return p2Artifacts;
    }

    protected File downloadP2ContentFor( final String groupId, final String artifactId, final String version )
        throws IOException
    {
        final File downloadDir = new File( "target/downloads/" + this.getClass().getSimpleName() );
        final File p2Content =
            downloadArtifact( groupId, artifactId, version, "xml", "p2Content", downloadDir.getCanonicalPath() );
        return p2Content;
    }

    protected File storageP2ArtifactsFor( final String groupId, final String artifactId, final String version )
        throws IOException
    {
        final File p2Artifacts =
            new File( new File( nexusWorkDir ), "storage/" + getTestRepositoryId() + "/" + groupId + "/" + artifactId
                + "/" + version + "/" + artifactId + "-" + version + "-p2Artifacts.xml" );
        return p2Artifacts;
    }

    protected File storageP2ContentFor( final String groupId, final String artifactId, final String version )
        throws IOException
    {
        final File p2Artifacts =
            new File( new File( nexusWorkDir ), "storage/" + getTestRepositoryId() + "/" + groupId + "/" + artifactId
                + "/" + version + "/" + artifactId + "-" + version + "-p2Content.xml" );
        return p2Artifacts;
    }

    protected File storageP2RepositoryArtifactsXML()
        throws IOException
    {
        final File p2Artifacts =
            new File( new File( nexusWorkDir ), "storage/" + getTestRepositoryId() + P2_REPOSITORY_ROOT_PATH
                + ARTIFACTS_XML );
        return p2Artifacts;
    }

    protected File storageP2RepositoryContentXML()
        throws IOException
    {
        final File p2Content =
            new File( new File( nexusWorkDir ), "storage/" + getTestRepositoryId() + P2_REPOSITORY_ROOT_PATH
                + P2Constants.CONTENT_XML );
        return p2Content;
    }

}