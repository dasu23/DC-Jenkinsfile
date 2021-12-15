#!groovy

@Library("DC-Jenkinsfile@master")

def remote = new org.devops.remote()


pipeline {
    agent any
    parameters {
         choice(name:"ip_address",description: '目标ip地址',choices: ['10.6.213.10', '10.6.213.20', '10.6.213.30','10.6.213.40','10.6.213.50'])
//        string(name: "ip_address", description: '目标ip地址', defaultValue: '')
        choice(name: "version", description: '安装版本号', choices: ['dce-4.0.7-35110', 'dce-4.0.7-35143'])
    }
    stages {
        stage("check remote health") {
            steps {
                script {
                    filename = "for file in \$(ls -t /root | grep ${version}) ; do echo \$file ;break; done || true"
                    dockerps = "docker ps | grep dce-etcd | grep -v pause | awk '{print \$1}'"

                    aa = remote.getShellPrama(filename)
                    println(aa)
                    bb = remote.getSSHPrama("ifconfig")
                    println(bb)
                }
            }
        }
    }
}