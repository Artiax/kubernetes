pipelineJob('jenkins') {
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
}
