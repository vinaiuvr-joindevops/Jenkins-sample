library 'reference-pipeline'
library 'AppserviceAccount'

pipeline{
    agent any

    options{
        buildDiscarder(logRotator(numToKeepStr: '05'))
    }

    tools{

        jdk 'java_8'
        maven 'Maven 3.3.3'
    }


    Parameters{
        // the default is always the first item in the list
        choice(name: 'CF_ENV', choices: 'default\ndevelopment\nrelease\nstaging\nproduction' , description: 'Target Environemnt')

       }

    environment{
        CF_APP_NAME = "sprint-boot-admin"
        CF_PAM_ID =  "188886"
        EAI_NUMBER = "3535146"
        EAI_NAME = "esi-common"
        //EAI folder is needed for nexus IQ
        EAI_FOLDER_NAME = "esi-common-3540433"
        APP_CM_EMP_IDS= "3949051,616655"


        APP_VERSION = " "
        GIT_BRANCH = "$(env.BRANCH_VALUE)"
        APP_GROUP = "eai3535146.com.example"
    }

    stages {
        stage('Initialize'){
            steps{
                script{
                    APP_GROUP = readMavenPom().getGroupID()
                    APP_VERSION = readMavenPom().getVersion()
                    APP_NAME = readMavenPom().getArtifactID()
                    if(GIT_BRANCH.contains('master')){
                        NEXUS_REPO = "release"
                        NEXUS_VERSION = "${APP_VERSION}"
                        RELEASE_FLAG =true
                    } else {
                        NEXUS_REPO = "staging"
                        NEXUS_VERSION = "${APP_VERSION}"
                        RELEASE_FLAG =true
                    }

                    printIn "Nexus Repo is ${NEXUS_REPO}"
                    printIN "Version is ${APP_VERSION}"
                }
            }
        }

        stage('BUILD') {
            steps {
                printIn "Building source from branch $(GIT_BRANCH)"
                /*sh 'chmod +x mvnw' */
                sh 'mvn clean install'
            }
        }

        stage('Nexus staging'){

            steps{
                printIn "uploading jar to Nexus ${CF_APP_NAME}.jar"
                nexusArtificatUpFolder artifacts: [[artifactID: "${CF_APP_NAME}", classifer: '', file:"target/${CF_APP_NAME}-${APP_VERSION}.jar", type:'jar']],
                credentialsID: "${NEXUS_CREDS_ID}",
                groupID: "${APP_GROUP}",
                nexusURL:'nexus.prod.cloud.fedex.com:8443/nexus',
                nexusVersion: 'Nexus3',
                protocol: 'https',
                repository: "$(NEXUS_REPO)",
                Version: "${NEXUS_VERSION}"
            }
        }

        stage('Nexus Pull'){
            steps{
                printIn "Downloading from nexus repo..."
                downloadNexusArtifact groupId: "${APP_GROUP}",
                    artifactID: "${CF_APP_NAME}",
                    repo: "${NEXUS_REPO}",
                    release: "${RELEASE_FLAG}" toBoolean(),
                    extension: "jar"
                    version: "${NEXUS_VERSION}"
                    downloadFileName: "${CF_APP_NAME}.jar"
                }
       }

       stage('PCF-deploy-development'){
        when{

            environment name: 'CF_ENV', value: 'development'
        }

        environment{
            CF_URI='https://api.sys.clwdev4-az3.pass.fedex.com'
            CF_SPACE='development'
            CF_INSTANCES='1'
            CF_MEMORY='1G'
            CF_PROFILE='L2'
            CF_HOSTNAME="${APP_NAME}-${CF_PROFILE}"
        }

        steps{
            pcfDeploy pamid: "$CF_PAM_ID",
            url: "$CF_URI",
            space: "$CF_SPACE",
            cfcmd: "push $APP_NAME "
        script{
            sh """#!/bin/bash
            export PATH=${PATH}:${WORKSPACE}
            
            cf set-env ${APP_NAME} SPRING_PROFILES_ACTIVE cloud,${CF_PROFILE}
            cf set-env ${APP_NAME} ACTUATOR_USER_NAME ${ACTUATOR_USER_NAME } 
            cf start ${APP_NAME}

            """
             }
          }
      }

    }       

    }





}   
