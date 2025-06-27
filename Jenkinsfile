pipeline {
    agent any

    triggers {
        githubPush() // 🔥 this tells Jenkins to react to webhook
    }

    environment {
        BRANCH_NAME = 'main'
        REPO_URL = 'https://github.com/CharradR/Groupe4-2ALINFO5-2425.git'
        SONAR_HOST_URL = 'http://192.168.33.10:9000'
        SONAR_TOKEN = credentials('SONAR_TOKEN')
    }

    stages {
        stage('Clean') {
            steps {
                sh 'rm -rf target'
            }
        }
        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('Unit Test') {
            steps {
                sh 'mvn clean test -Dspring.profiles.active=test'
            }
        }
        stage('SonarQube ') {
            steps {
                sh """
                   mvn sonar:sonar \
                   -Dsonar.projectKey=alinfo5-groupe4 \
                   -Dsonar.host.url=http://192.168.33.10:9000 \
                   -Dsonar.login=26b061e51ef575077ec8b16579a1c73ef9583c3f
                """
            }
        }
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
        stage('Upload Artifacts to Nexus') {
            steps {
                script {
                    def dynamicVersion = "${env.BUILD_NUMBER}"
                    def jarFile = "target/Foyer-${dynamicVersion}.jar"
                    sh "cp target/Foyer-0.0.1.jar ${jarFile}"

                    nexusArtifactUploader(artifacts: [[artifactId: 'Foyer',
                                                        classifier: '',
                                                        file: "${jarFile}",
                                                        type: 'jar']],
                                            credentialsId: 'nexus-creds',
                                            groupId: 'com.example',
                                            nexusUrl: '192.168.33.10:8081',
                                            nexusVersion: 'nexus3',
                                            protocol: 'http',
                                            repository: 'Foyer',
                                            version: "${dynamicVersion}"
                                            )
                }
            }
        }
    }

    post {
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed.'
        }
    }
}