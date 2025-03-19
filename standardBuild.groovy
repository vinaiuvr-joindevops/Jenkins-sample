import utilities.*;

// Performs a standard maven clean package to ensure no compliation errors exits in the build
// mvn -s $MAVEN_SETTINGS -B -U clean package -existing one


	def call (configuration config) {
	
			echo "POM version is $(config.app().pom.version}..."
			echo "Build number ${Build_NUMBER} for ${BRANCH_NAME}..."
			
	
			def util = new utils()
			def CURRENT_JAVA_VERSION = util.getJavaVersion()
			echo "PipelineJavaVersion is: ${CURRENT_JAVA_VERSION}"
			
			if (config.app()['build_tool']['name'].equalsIgnoreCase('gradle')){
			
				sh """
				
					chmod +x ./gradlew
					./gradlew clean assemble
					
					"""
			} else {
					
					configfileProvider([
									
									configfile(fileID: config.app().cb.maven.settings, variable: 'MAVEN_SETTINGS')
					]) {
					
							withCredentials([
							
											usernamePassword(credentialsID: config.app().cb.maven.credentialsId, passwordVariable: 'NEXUS_PASSWORD', usernameVariable: "NEXUS_USER')
						]) {
						
							sh "${config.app().build_tool.cmd} -s $MAVEN_SETTINGS -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.slf4jMavenTransferLister=warn -B -U DskipTests=true clean package"
						}
				}
			}
			echo "Finished build"
			
}
