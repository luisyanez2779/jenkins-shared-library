#!/user/bin/env groovy

def call() {
    echo 'building the application from inside shared library...'
    sh 'mvn package'
}