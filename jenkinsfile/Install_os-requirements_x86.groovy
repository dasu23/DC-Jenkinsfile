#!groovy

@Library("DC-Jenkinsfile@master")
def remote = new org.devops.remote()


def testconnect(ip_address) {
    env.testconnect = sh(returnStdout: true, script: 'sshpass -p dangerous ssh -o StrictHostKeyChecking=no root@${ip_address} \'echo \"测试登录\"\' || true').trim()
    if ("${testconnect}".size() == 0) {
        error "-------------------------目标服务器无法连通，请确认服务器ip地址是否正确-------------------------"
    } else {
        echo "测试登录成功"
    }
}

pipeline {
    agent any
    parameters {
        // string(name:"ip_address",description: '目标ip地址',choices: ['10.6.213.10', '10.6.213.20', '10.6.213.30','10.6.213.40','10.6.213.50'])
        string(name: "ip_address", description: '目标ip地址', defaultValue: '')
        choice(name: "version", description: '安装版本号', choices: ['dce-4.0.7-35110', 'dce-4.0.7-35143'])
    }
    stages {
        stage("check remote health") {
            steps {
                script {
                    // 获取安装包名称，并判断是否存在
                    env.file = sh(returnStdout: true, script: 'for file in \$(ls -t /root | grep ${version}) ; do echo \$file ;break; done || true').trim()
                    if ("${file}".size() == 0) {
                        error "-------------------------所选安装包不存在于服务器，请先下载安装包-------------------------"
                    } else {
                        println("安装包名称: ${file}")
                    }
                    // 检查目标机是否正确
                    testconnect("${ip_address}")
                    // 登录节点初始化
                    rserver = remote.GetRemoteServer("${ip_address}")
                }
            }
        }
        stage('Get Dce') {
            steps {
                script {
                    echo "------------------------查询目标机是否存在最新安装包------------------------"
                    env.ls1 = "ls /root | grep ${file} || true"
                    env.hastar = sh(returnStdout: true, script: 'sshpass -p dangerous ssh -o StrictHostKeyChecking=no root@${ip_address} ${ls1}').trim()
                    if ("${hastar}".size() == 0) {
                        // 拉取安装包
                        sh '''
                          SSHPASS="dangerous" sshpass -e  scp -r /root/$file root@${ip_address}:/root
                        '''
                        println("拷贝安装包完成")
                    } else {
                        println("已存在安装包，不再重复拉取")
                    }
                }
            }
        }
        stage('tar Dce') {
            steps {
                script {
                    echo "------------------------查询安装包是否解压------------------------"
                    // 去除后缀获取文件夹名称
                    env.filename = sh(returnStdout: true, script: 'echo ${file} | sed -e \"s/.tar.gz//g\"').trim()
                    // 查询是否有相同文件夹名称
                    env.ls2 = "ls /root | grep ${filename} | grep -v tar || true"
                    env.hastar = sh(returnStdout: true, script: 'sshpass -p dangerous ssh -o StrictHostKeyChecking=no root@${ip_address} ${ls2}').trim()
                    if ("${hastar}".size() == 0) {
                        sh '''
                            sshpass -p dangerous ssh -o StrictHostKeyChecking=no root@${ip_address} 'tar -xvf '$file''
                          '''
                        println("文件解压完成")
                    } else {
                        println("文件已解压，不再重复解压")
                    }
                }
            }
        }
        stage('Install os-requirements') {
            steps {
                script {
                    echo "------------------------安装os------------------------"
                    env.dockerinfo = sh(returnStdout: true, script: 'sshpass -p dangerous ssh -o StrictHostKeyChecking=no root@${ip_address} \'docker images || true\'').trim()
                    if ("${dockerinfo}".size() == 0) {
                        sshCommand remote: rserver, command: "cd ${filename} && chmod +x ./os-requirements && ./os-requirements -y -q"
                        println("os-requirements安装完成")
                    } else {
                        println("----------已安装docker，系统默认为已安装os-requirements，结束安装任务----------")
                    }
                }
            }
        }
    }
}

