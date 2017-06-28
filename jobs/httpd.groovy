pipelineJob('images/httpd') {
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
            scriptPath('images/httpd/Jenkinsfile')
        }
    }
    configure {
       it / definition / lightweight(true)
    }
}

pipelineJob('deployments/httpd') {
    parameters {
        stringParam('TAG','default')
    }
    definition {
        cps {
            script($/
                echo 'Spawning a slave for this job...'

                node('docker') {
                    stage('Clone') {
                        git branch: 'development', url: 'https://github.com/Artiax/kubernetes.git', changelog: false, poll: false
                    }

                    stage('Deploy') {
                        dir('deployments') {
                            sh 'sed -ri "s/(image:.*httpd:)[a-z0-9\.]*/\1$$TAG/" httpd.yaml'
                            sh 'kubectl apply --record -f httpd.yaml'
                        }
                    }
                }
            /$.stripIndent())
            sandbox(true)
        }
    }
}
