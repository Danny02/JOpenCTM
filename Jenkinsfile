node {
	stage "checkout"
	checkout scm

	stage "build"
  docker.image('maven:alpine').inside {
    sh 'mvn -B  package -DlocalRepositoryPath=./.m2repo'
  }
}