pipelineJob('images/jenkins') {
    definition {
        cpsScm {
            scm {
                git {
                    branch('development')
                    remote {
                        url('https://github.com/Artiax/kubernetes.git')
                    }
                }
            }
            scriptPath('images/jenkins/Jenkinsfile')
        }
    }
    configure {
       it / definition / lightweight(true)
    }
}

pipelineJob('images/docker-slave') {
    definition {
        cpsScm {
            scm {
                git {
                    branch('development')
                    remote {
                        url('https://github.com/Artiax/kubernetes.git')
                    }
                }
            }
            scriptPath('images/docker-slave/Jenkinsfile')
        }
    }
    configure {
       it / definition / lightweight(true)
    }
}

pipelineJob('deployments/jenkins') {
    parameters {
        stringParam('TAG','default')
    }
    definition {
        cps {
            script("""
                echo 'Spawning a slave for this job...'

                node('docker') {
                    stage('Clone') {
                        git branch: 'development', url: 'https://github.com/Artiax/kubernetes.git', changelog: false, poll: false
                    }

                    stage('Deploy') {
                        dir('deployments') {
                            sh 'sed -ri "s/(image:.*jenkins:)[a-z0-9\\\.]*/\\\1$TAG/" jenkins.yaml'
                            sh 'kubectl apply --record -f jenkins.yaml'
                        }
                    }
                }
            """.stripIndent())
            sandbox(true)
        }
    }
}
