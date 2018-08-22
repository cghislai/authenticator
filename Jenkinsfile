pipeline {
    agent any
    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip tests')
        booleanParam(name: 'NPM_DEPLOY', defaultValue: false, description: 'NPM deployment')
        string(name: 'ALT_DEPLOYMENT_REPOSITORY', defaultValue: 'central@valuya::default::https://nexus.valuya.be/nexus/repository/maven-snapshots/',
         description: 'Alternative deployment repo')
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage ('Build') {
            steps {
                withMaven(maven: 'maven', mavenSettingsConfig: 'nexus-mvn-settings') {
                    sh "mvn -DskipTests=${params.SKIP_TESTS} clean compile install"
                }
            }
        }
        stage ('Publish') {
            steps {
                script {
                    env.MVN_ARGS=""
                    env.NPM_DEPLOY=params.NPM_DEPLOY
                    if (params.ALT_DEPLOYMENT_REPOSITORY != '') {
                        env.MVN_ARGS="-DaltDeploymentRepository=${params.ALT_DEPLOYMENT_REPOSITORY}"
                    }
                }
                withMaven(maven: 'maven', mavenSettingsConfig: 'nexus-mvn-settings',
                          mavenOpts: '-DskipTests=true') {
                    sh "mvn deploy $MVN_ARGS"
                }
                nodejs(nodeJSInstallationName: 'node 10', configId: 'npmrc-@charlyghislain') {  catchError {
                  ansiColor('xterm') {
                    sh '''
                       [ "$NPM_DEPLOY" != "true" ] && exit 0
                       cd authenticator-api/target/npm
                       npm publish --access=public || echo "skipping.."
                       cd ../../..

                       cd authenticator-admin-api/target/npm
                       npm publish --access=public || echo "skipping.."
                       cd ../../..

                       cd authenticator-application-api/target/npm
                       npm publish --access=public || echo "skipping.."
                       cd ../../..

                       cd authenticator-management-api/target/npm
                       npm publish --access=public || echo "skipping.."
                       cd ../../..

                        '''
                  }
                }}
            }
        }
    }
}
