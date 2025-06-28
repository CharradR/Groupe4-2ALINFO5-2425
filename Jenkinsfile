pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        SONAR_HOST_URL = 'http://192.168.33.10:9000'
        SONAR_TOKEN = credentials('SONAR_TOKEN2')
        EMAIL_RECIPIENTS = 'raed.charrad91@gmail.com'
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

        stage('Quality Gate Check') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    script {
                        def result = waitForQualityGate()
                        echo "✅ Quality Gate status: ${result.status}"
                        if (result.status != 'OK') {
                            currentBuild.result = 'FAILURE'
                            error "❌ Quality Gate failed: ${result.status}"
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

        stage('Package and Upload to Nexus') {
            when {
                expression { currentBuild.resultIsBetterOrEqualTo('SUCCESS') }
            }
            steps {
                script {
                    def version = "${env.BUILD_NUMBER}"
                    def jarFile = "target/Foyer-${version}.jar"
                    echo "📦 Packaging..."
                    sh "mvn package -DskipTests"
                    sh "cp target/Foyer-0.0.1.jar ${jarFile}"

                    echo "🚀 Uploading to Nexus..."
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
                    emailext (
                        to: "${EMAIL_RECIPIENTS}",
                        subject: "🚨 Jenkins Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        body: """<p><b>Project:</b> ${env.JOB_NAME}</p>
                                 <p><b>Build Number:</b> ${env.BUILD_NUMBER}</p>
                                 <p><b>URL:</b> <a href="${env.BUILD_URL}">${env.BUILD_URL}</a></p>
                                 <p><b>Status:</b> ${currentBuild.currentResult}</p>""",
                        mimeType: 'text/html'
                    )
                }
                always {
                    echo '🧹 Cleaning up workspace.. ..'
                }
    }
}
