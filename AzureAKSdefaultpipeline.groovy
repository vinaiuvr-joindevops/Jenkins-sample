#!groovy
library 'reference-pipeline'
library 'AppServiceAccount'

import utilites.*;

def call(Map entryConfig){
	def globalConfig = new Configuration()
	
	
	pipeline {
			agent {
			
				label 'CSI-DOCKER||docker'
			
			}
			tools {
			
				jdk 'java_17'
				maven 'MAVEN 4.0.0'
			}
			
			parameters{
			
					choice(name: 'Level', choices: "L1\nL2\nL3\nEPH\nNPE\", description: 'Please select level')
					choice(name: 'Operation", choices: "Publish and Deploy \npublish only \nDeploy only' , description: 'Action')
			}
			
			environment{
				MAVEN_SETTINGS = "AUTOBHAN_MAVEN_SETTINGS"
				AZURE_CLIENT_ID = credentials('ACR_AKS_CLIENT_ID_NPE')
				AZURE_CLIENT_SECRET = credentials('ACR_AKS_CLIENT_SECRET_NPE')
				AZURE_TENANT_ID = credentials('ACR_TENANT_ID')
				ACR_NAME = credentials('ACR_NAME_NPE')
				AZURE_SUBSCRIPTION = credentials('AZURE_SUBSCRIPTION_NPE')
				
			}
			triggers {snapshotDependencies()}
			
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
			
			
