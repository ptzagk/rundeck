package org.rundeck.plugin.scm.git

import org.eclipse.jgit.util.FileUtils
import spock.lang.Specification

/**
 * Created by greg on 8/31/15.
 */
class GitExportPluginFactorySpec extends Specification {

    File tempdir

    def setup() {
        tempdir = File.createTempFile("GitExportPluginFactorySpec", "-test")
        tempdir.delete()
    }

    def cleanup() {
        if (tempdir.exists()) {
            FileUtils.delete(tempdir, FileUtils.RECURSIVE)
        }
    }

    def "base description"() {
        given:
        def factory = new GitExportPluginFactory()
        def desc = factory.description

        expect:
        desc.title == 'Git Export'
        desc.name == 'git-export'
        desc.properties.size() == 6
    }

    def "base description properties"() {
        given:
        def factory = new GitExportPluginFactory()
        def desc = factory.description

        expect:
        desc.properties*.name == [
                'pathTemplate',
                'url',
                'branch',
                'committerName',
                'committerEmail',
                'format'
        ]
    }

    def "setup properties without basedir"() {
        given:
        def factory = new GitExportPluginFactory()
        def properties = factory.getSetupProperties()

        expect:
        properties*.name == [
                'pathTemplate',
                'url',
                'branch',
                'committerName',
                'committerEmail',
                'format'
        ]
    }

    def "setup properties with basedir"() {
        given:
        def factory = new GitExportPluginFactory()
        def tempdir = File.createTempFile("blah", "test")
        tempdir.deleteOnExit()
        tempdir.delete()
        def properties = factory.getSetupPropertiesForBasedir(tempdir)

        expect:
        properties*.name == [
                'dir',
                'pathTemplate',
                'url',
                'branch',
                'committerName',
                'committerEmail',
                'format'
        ]
        properties.find { it.name == 'dir' }.defaultValue == new File(tempdir.absolutePath, 'scm').absolutePath
    }

    def "create plugin"(){
        given:

        def factory = new GitExportPluginFactory()
        def gitdir = new File(tempdir, 'scm')
        def origindir = new File(tempdir, 'origin')
        def config = [
                dir           : gitdir.absolutePath,
                pathTemplate  : '${job.group}${job.name}-${job.id}.xml',
                branch        : 'master',
                committerName : 'test user',
                committerEmail: 'test@example.com',
                url           : origindir
        ]

        //create a git dir
        def git = GitExportPluginSpec.createGit(origindir)

        git.close()

        when:
        def plugin = factory.createPlugin(config, 'testproject')

        then:
        null!=plugin

        gitdir.isDirectory()
        new File(gitdir, '.git').isDirectory()

    }
}
