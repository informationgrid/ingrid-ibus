pipeline {
    agent any
    triggers{ cron( getCronParams() ) }

    tools {
        jdk 'jdk25'
    }

    environment {
        VERSION = readMavenPom().getVersion()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '5'))
    }

    stages {
        // normal build if it's not the master branch and not the support branch, except if it's a SNAPSHOT-version
        stage('Build-SNAPSHOT') {
            when {
                not { branch 'master' }
                not { buildingTag() }
                not {
                    allOf {
                        branch 'support/*'
                        expression { return !VERSION.endsWith("-SNAPSHOT") }
                    }
                }
            }
            steps {
                withMaven(
                    // Maven installation declared in the Jenkins "Global Tool Configuration"
                    maven: 'Maven3',
                    // Maven settings.xml file defined with the Jenkins Config File Provider Plugin
                    // Maven settings and global settings can also be defined in Jenkins Global Tools Configuration
                    mavenSettingsConfig: '2529f595-4ac5-44c6-8b4f-f79b5c3f4bae'
                ) {

                    echo "Project version: $VERSION"

                    // Run the maven build
                    sh 'mvn clean deploy -PrequireSnapshotVersion,docker,docker-$GIT_BRANCH -Dmaven.test.failure.ignore=true'

                } // withMaven will discover the generated Maven artifacts, JUnit Surefire & FailSafe & FindBugs reports...
            }
        }
        // release build if it's the master or the support branch and is not a SNAPSHOT version
        stage ('Build-Release') {
            when {
                anyOf { branch 'master'; branch 'support/*' }
                expression { return !VERSION.endsWith("-SNAPSHOT") }
                not { buildingTag() }
            }
            steps {
                withMaven(
                    maven: 'Maven3',
                    mavenSettingsConfig: '2529f595-4ac5-44c6-8b4f-f79b5c3f4bae'
                ) {
                    echo "Release: $VERSION"
                    // check license
                    // check is release version
                    // deploy to distribution
                    // send release email
                    sh 'mvn clean deploy -Pdocker,release'
                }
            }
        }

        stage('Stage Tag Building') {
            when { buildingTag() }
            steps {
                script {
                    if (env.BUILD_NUMBER == '1') {
                        env.EXTRA_TAG = "${env.TAG_NAME}-release"
                    } else {
                        env.EXTRA_TAG = "${env.TAG_NAME}"
                    }
                }
                withMaven(
                    maven: 'Maven3',
                    mavenSettingsConfig: '2529f595-4ac5-44c6-8b4f-f79b5c3f4bae'
                ) {
                    echo "Scheduled Release: $VERSION"
                    // only build and create docker image
                    // use release tag if build number == 1
                    sh "mvn package docker:build -DpushImageTag -DdockerImageTags=${env.EXTRA_TAG} -Pdocker -DskipTests"
                }
            }
        }

        stage ('Create SBOMs') {
            when {
                anyOf {
                    branch 'develop'
                    buildingTag()
                }
            }
            steps {
                withMaven(
                    maven: 'Maven3',
                    mavenSettingsConfig: '2529f595-4ac5-44c6-8b4f-f79b5c3f4bae'
                ) {
                    sh "mvn cyclonedx:makeAggregateBom "
                    sh "mvn cyclonedx:makeAggregateBom -D includeTestScope=true -D outputName=bom-dev"
                }
                script {
                    def imageToScan = "bellsoft/liberica-openjre-alpine:25"

                    docker.withRegistry('https://registry.opencode.de', 'registry-opencode') {
                        sh """
                            docker run --rm --pull=always --volumes-from jenkins anchore/syft:latest ${imageToScan} --output cyclonedx-json=${WORKSPACE}/target/sbom-docker-image.json
                        """
                    }
                }
            }
        }

        stage ('Merge SBOMs') {
            when {
                anyOf {
                    branch 'develop'
                    buildingTag()
                }
            }
            agent {
                docker {
                    image 'cyclonedx/cyclonedx-cli:latest'
                    // CRITICAL: Overrides the entrypoint so Jenkins can run it as a regular shell agent
                    args '-u root --entrypoint=""'
                    reuseNode true
                }
            }
            steps {
                script {
                    sh """
                        cyclonedx merge --input-files target/bom.json target/sbom-docker-image.json --output-file target/sbom-docker.json --output-format json --output-version v1_6 --hierarchical --group de.ingrid --name ingrid-iplug-blp --version ${determineVersion()}
                    """
                }
            }
        }

        stage ('Upload SBOM') {
            when {
                anyOf {
                    branch 'develop'
                    buildingTag()
                }
            }
            steps {
                script {
                    def parentId = '02f3b12f-f381-4db3-9457-8a39309dd5ff'
                    def componentName = 'ingrid-ibus'

                    // Attach SBOM to Docker Image
                    withCredentials([
                            file(credentialsId: 'cosign-private-key', variable: 'COSIGN_KEY_FILE'),
                            string(credentialsId: 'cosign-key-password', variable: 'COSIGN_PASSWORD'),
                            usernamePassword(credentialsId: 'registry-opencode', usernameVariable: 'REG_USER', passwordVariable: 'REG_PASS')
                        ]) {
                        if (env.TAG_NAME) {
                            env.VERSION = env.TAG_NAME
                        } else if (BRANCH_NAME == 'develop') {
                            env.VERSION = 'latest'
                        } else {
                            env.VERSION = BRANCH_NAME.replaceAll('/', '-')
                        }
                        echo "VERSION: ${env.VERSION}"
                        def imageDigest = sh(script: "docker inspect --format='{{index .RepoDigests 0}}' registry.opencode.de/informationgrid/${componentName}:${env.VERSION} | cut -d'@' -f2", returnStdout: true).trim()
                        echo "IMAGE_DIGEST: ${env.IMAGE_DIGEST}"
                        def imageToScan = "registry.opencode.de/informationgrid/${componentName}@${imageDigest}"
                        sh """
                            docker run -u 0:0 --rm \
                              -e COSIGN_PASSWORD \
                              --volumes-from jenkins \
                              ghcr.io/sigstore/cosign/cosign:v3.1.3 attest \
                              --type cyclonedx \
                              --predicate \${WORKSPACE}/target/sbom-docker.json \
                              --key "\$COSIGN_KEY_FILE" \
                              --registry-username "\$REG_USER" \
                              --registry-password "\$REG_PASS" \
                              ${imageToScan}
                        """
                    }

                    // Dependency Track
                    withCredentials([string(credentialsId: 'api-token-dependency-track', variable: 'API_KEY')]) {
                        def baseVersion = determineVersion()
                        def sbomConfigs = [
                            [artifact: 'target/bom.json',        suffix: '',              tag: 'ingrid_deps_prod'],
                            [artifact: 'target/bom-dev.json',    suffix: '-dev',          tag: 'ingrid_deps_dev'],
                            [artifact: 'target/sbom-docker.json', suffix: '-docker-image', tag: 'ingrid_deps_docker']
                        ]

                        sbomConfigs.each { cfg ->
                            def props = [group: 'InGrid', parentId: parentId, tags: ['ingrid', cfg.tag], isLatest: cfg.suffix == '']
                            dependencyTrackPublisher(artifact: cfg.artifact, projectName: componentName, projectVersion: baseVersion + cfg.suffix, synchronous: true, dependencyTrackApiKey: API_KEY, projectProperties: props)
                        }
                    }
                }
            }
        }

        stage ('Build RPM') {
            when { expression { return shouldBuildDevOrRelease() } }
            agent {
                docker {
                    image 'docker-registry.wemove.com/ingrid-rpmbuilder'
                    reuseNode true
                }
            }
            steps {
                script {
                    sh "sed -i 's/^Version:.*/Version: ${determineRpmVersion()}/' rpm/ingrid-ibus.spec"
                    sh "sed -i 's/^Release:.*/Release: ${determineRpmReleasePart()}/' rpm/ingrid-ibus.spec"

                    // Prepare build
                    sh "mkdir -p ./target/rpms /root/rpmbuild/SPECS"
                    sh """
                        cp ${WORKSPACE}/rpm/ingrid-ibus.spec /root/rpmbuild/SPECS/ingrid-ibus.spec &&
                        rpmbuild -bb /root/rpmbuild/SPECS/ingrid-ibus.spec
                    """

                    withCredentials([
                            file(credentialsId: 'ingrid-rpm-public', variable: 'RPM_PUBLIC_KEY'),
                            file(credentialsId: 'ingrid-rpm-private', variable: 'RPM_PRIVATE_KEY'),
                            string(credentialsId: 'ingrid-rpm-passphrase', variable: 'RPM_SIGN_PASSPHRASE')
                        ]) {
                        sh 'gpg --batch --import $RPM_PUBLIC_KEY'
                        sh 'gpg --batch --import $RPM_PRIVATE_KEY'
                        sh "mkdir -p ./target/rpms/ingrid"
                        sh "cp -r /root/rpmbuild/RPMS/noarch/* ${WORKSPACE}/target/rpms/ingrid/"
                        sh "expect /rpm-sign.exp ${WORKSPACE}/target/rpms/ingrid/*.rpm"

                        archiveArtifacts artifacts: 'target/rpms/ingrid/ingrid-ibus-*.rpm', fingerprint: true
                    }
                }
            }
        }

        stage('Deploy RPM') {
            when { expression { return shouldBuildDevOrRelease() } }
            steps {
                script {
                    def repoType = env.TAG_NAME ? "rpm-ingrid-releases" : "rpm-ingrid-snapshots"
                    sh "mv backend/target/bom.json backend/target/ingrid-ibus-${determineRpmVersion()}.sbom.json"
                    archiveArtifacts artifacts: "backend/target/*.sbom.json", fingerprint: true

                    withCredentials([usernamePassword(credentialsId: '9623a365-d592-47eb-9029-a2de40453f68', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                        sh '''
                            curl -f --user $USERNAME:$PASSWORD --upload-file target/rpms/ingrid/*.rpm https://nexus.informationgrid.eu/repository/''' + repoType + '''/
                            curl -f --user $USERNAME:$PASSWORD --upload-file backend/target/*.sbom.json https://nexus.informationgrid.eu/repository/''' + repoType + '''/
                        '''
                    }
                }
            }
        }

        stage ('SonarQube Analysis'){
            when { branch 'develop' }
            steps {
                withMaven(
                    maven: 'Maven3',
                    mavenSettingsConfig: '2529f595-4ac5-44c6-8b4f-f79b5c3f4bae'
                ) {
                    withSonarQubeEnv('Wemove SonarQube') {
                        sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar'
                    }
                }
            }
        }
    }

    post {
        changed {
            // send Email with Jenkins' default configuration
            script {
                emailext (
                    body: '${DEFAULT_CONTENT}',
                    subject: '${DEFAULT_SUBJECT}',
                    to: '${DEFAULT_RECIPIENTS}')
            }
        }
    }
}

def getCronParams() {
    String tagTimestamp = env.TAG_TIMESTAMP
    long diffInDays = 0
    if (tagTimestamp != null) {
        long diff = "${currentBuild.startTimeInMillis}".toLong() - "${tagTimestamp}".toLong()
        diffInDays = diff / (1000 * 60 * 60 * 24)
        echo "Days since release: ${diffInDays}"
    }

    def versionMatcher = /\d\.\d\.\d(.\d)?/
    if( env.TAG_NAME ==~ versionMatcher && diffInDays < 180) {
        // every Sunday between midnight and 6am
        return 'H H(0-6) * * 0'
    }
    else {
        return ''
    }
}

def shouldBuildDevOrRelease() {
    // If no tag is being built OR it is the first build of a tag
    boolean isTag = env.TAG_NAME != null && env.TAG_NAME.trim() != ''
    return !isTag || (isTag && currentBuild.number == 1)
}

def determineVersion() {
    if (env.TAG_NAME) {
        if (env.TAG_NAME.startsWith("RPM-")) { // e.g. RPM-8.0.0-0.1SNAPSHOT
            def lastDashIndex = env.TAG_NAME.lastIndexOf("-")
            return env.TAG_NAME.substring(4, lastDashIndex)
        }
        return env.TAG_NAME
    } else {
        return env.BRANCH_NAME.replaceAll('/', '_')
    }
}

def determineRpmVersion() {
    return determineVersion().replaceAll('-', '_')
}

def determineRpmReleasePart() {
    if (env.TAG_NAME) {
        if (env.TAG_NAME.startsWith("RPM-")) {
            return env.TAG_NAME.substring(env.TAG_NAME.lastIndexOf("-") + 1)
        }
        return '1'
    } else {
        return 'SNAPSHOT'
    }
}
