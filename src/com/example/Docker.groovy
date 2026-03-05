#!/user/bin/env groovy
package com.example

class Docker implements Serializable {

    def script

    Docker(script) {
        this.script = script
    }

    def buildJar() {
        script.echo 'building JAR from inside shared library...'
        script.sh 'mvn package'
    }

    def buildDockerImage(String imageName) {
        script.echo "building the docker image $imageName from Docker class..."
        script.sh "docker build -t $imageName ."
     }

    def dockerLogin(String imageName) {
        script.echo "docker login for $imageName from Docker class..."
        script.withCredentials([
            script.usernamePassword(
                credentialsId: 'docker-hub-repo',
                passwordVariable: 'PASS',
                usernameVariable: 'USER'
            )
        ]) {
            script.sh "echo '${script.PASS}' | docker login -u '${script.USER}' --password-stdin"
        }
    }

    def dockerPush(String imageName) {
        script.echo "docker push for $imageName from Docker class..."
        script.sh "docker push $imageName"
    }

    def deployToEC2(String imageName) {
        script.echo "deploying docker image to EC2"
        def dockerCmd = "docker run -d -p 8080:8080 ${imageName}"
        script.sshagent(['ec2-server-key']) {
            script.sh "ssh -o StrictHostKeyChecking=no ec2-user@18.217.58.32 ${dockerCmd}"
        }
    }
}