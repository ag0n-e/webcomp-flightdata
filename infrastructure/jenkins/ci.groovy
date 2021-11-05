pipeline {
	agent any
	options {
		ansiColor('xterm')
	}
	// TODO: Delete either all yarn or all npm scripts
	stages {
		stage('Agent: Node Docker') {
			agent {
				dockerfile {
					filename 'infrastructure/docker/node.dockerfile'
					additionalBuildArgs '--build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins)'
				}
			}
			stages {
				stage('Dependencies') {
					steps {
						sh 'yarn'
						sh 'npm ci'
					}
				}
				stage('Test') {
					steps {
						sh '''
							yarn lint
							yarn test
						'''
						sh '''
							npm run lint
							npm run test
						'''
					}
				}
				stage('Build') {
					steps {
						sh 'yarn build'
						sh 'npm run build'
					}
				}
			}
		}
		stage('CC: REUSE') {
			steps {
				sh 'reuse lint'
			}
		}
	}
}
