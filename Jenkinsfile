pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        SONAR_HOST_URL = 'http://192.168.33.10:9000'
        SONAR_TOKEN = credentials('SONAR_TOKEN2')
    }

    options {
        skipStagesAfterUnstable()
        timestamps()
    }

    stages {
        stage('Preparation') {
            steps {
                echo "🔄 Cleaning workspace..."
                deleteDir()
            }
        }

        stage('Checkout') {
            steps {
                echo "📥 Cloning repository..."
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                echo "🔧 Compiling the project..."
                sh 'mvn compile'
            }
        }

        stage('Unit Test') {
            steps {
                echo "🧪 Running tests..."
                sh 'mvn test -Dspring.profiles.active=test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                            sh 'mvn sonar:sonar -Dsonar.projectKey=alinfo5-groupe4-2'
                        }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    script {
                        def result = waitForQualityGate()
                        echo "Quality Gate status: ${result.status}"
                        if (result.status != 'OK') {
                            error "Quality Gate failed: ${result.status}"
                        }
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo "📦 Packaging the application..."
                sh 'mvn package -DskipTests'
            }
        }

        stage('Upload to Nexus') {
            steps {
                script {
                    def version = "${env.BUILD_NUMBER}"
                    def jarFile = "target/Foyer-${version}.jar"
                    sh "cp target/Foyer-0.0.1.jar ${jarFile}"

                    nexusArtifactUploader(
                        artifacts: [[
                            artifactId: 'Foyer',
                            classifier: '',
                            file: jarFile,
                            type: 'jar'
                        ]],
                        credentialsId: 'nexus-creds',
                        groupId: 'com.example',
                        nexusUrl: '192.168.33.10:8081',
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        repository: 'Foyer',
                        version: version
                    )
                }
            }
        }
    }

    post {
        success {
            echo '🎉 Build completed successfully!'
        }
        failure {
            echo '💥 Build failed.'
        }
        always {
            echo '🧹 Cleaning up...'
        }
    }
}
