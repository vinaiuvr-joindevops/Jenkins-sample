#!groovy
library 'reference-pipeline'
library 'AppServiceAccount'


import utilites.*;

def call(Map entryConfig){
	def globalConfig = new Configuration()
	
	pipeline {
			agent {
					label 'CSI-DOCKER||docker'
					docker {
							label 'CSI-DOCKER||docker'
							image 'nexus2.prod.cloud.fedex.com:8444/fdx/jenkins/default-tools-image'
				}
			}
			// if docker image cannot be pulled, swithch to any agent	
			// agent 'any'
			//
			tools {
				jdk 'java_17'
				maven 'Maven 4.0.0'
			}
			
			options {
					disableConcurrentBuilds()
					buildDiscarder(logRotator(numToKeepStr: "s"))
					
				}
				
			triggers {snapshotDependencies() }
			
			stages {
					stage ('BootStrap Configuration') {
						
							steps {
									script{
											checkout scm
											entryConfig.projectType = "java"
											globalConfig = initConfiguration(entryConfig)
											postInit(globalConfig)
											
										}
									}
								}
								
					stage('Deployment Selection'){
						when {
							expression { globalConfig.shoulSelectDeployment()}
						}
						steps {
							script {deploymentSelection(globalConfig)}
						}
					}
					
					stage('Build') {
							tools {
							jdk globalConfig.app()['pom']['javaversion'];
						}
						
							when {
									expression {globalConfig.isNotSkipped('build') }
								}
								
							steps {
									script { buildSteps(globalConfig) }
								}
						}
					

					stage('Test') {
							tools {
							jdk globalConfig.app()['pom']['javaversion'];
						}
						
							when {
									expression {globalConfig.isNotSkipped('test') }
								}
								
							steps {
									script { testSteps(globalConfig) }
								}
						}
					
					stage('code Quality') {
							tools {
							jdk globalConfig.app()['pom']['javaversion'];
						}
						
							when {
									expression {globalConfig.isNotSkipped('codeQuality') }
								}
								
							steps {
									script { codeQualitySteps(globalConfig) }
								}
						}
					
					stage('Nexus staging') {
							tools {
							jdk globalConfig.app()['pom']['javaversion'];
						}
						
							when {
									expression {globalConfig.isNotSkipped('nexus') }
								}
								
							steps {
									script { nexusSteps(globalConfig) }
								}
						}	
								
					stage('tag') {
							tools {
							jdk globalConfig.app()['pom']['javaversion'];
						}
						
							when {
									expression {globalConfig.isNotSkipped('tag') }
								}
								
							steps {
									script { tagSCMSteps(globalConfig) }
								}
						}
						
					stage('Distribute Artifact') {
							tools {
							jdk globalConfig.app()['pom']['javaversion'];
						}
						
							when {
									expression {globalConfig.isNotSkipped('distribute') }
								}
								
							steps {
									script { distributeSteps(globalConfig) }
								}
						}
						
					stage('Deploy') {
							tools {
							jdk globalConfig.app()['pom']['javaversion'];
						}
						
							when {
									expression {globalConfig.isNotSkipped('deploy') && globalconfig.deployEnabled() }
								}
								
							steps {
									script { deploySteps(globalConfig) }
								}
						}					
					post {
							failure {
									script { postSteps(globalConfig, 'failure') }
									}
							
							success {
									script { postSteps(globalConfig, 'success') }
									}
							always {
									script { postSteps(globalConfig, 'always') }
									}		
									
					}
			}
}	
