#!/user/bin/env groovy
package com.example

class Docker implements Serializable {

    def script

    Docker(script) {
        this.script = script
    }

    def buildJar() {
        script.echo 'building the application from inside shared library...'
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
            script.sh "docker build -t $imageName ."
            script.sh "echo '${script.PASS}' | docker login -u '${script.USER}' --password-stdin"
            script.sh "docker push $imageName"
        }
    }

    def dockerPush(String imageName) {
        script.echo "docker push for $imageName from Docker class..."
        script.sh "docker push $imageName"
    }
}