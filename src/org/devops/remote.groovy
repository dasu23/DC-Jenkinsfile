package org.devops

def GetRemoteServer(ip) {
    def remote = [:]
    remote.name = ip
    remote.host = ip
    remote.port = 22
    remote.pty = true
    remote.allowAnyHosts = true
    withCredentials([usernamePassword(credentialsId: '74b321a0-01ca-4629-ad90-e3c47c1cd354', passwordVariable: 'password', usernameVariable: 'userName')]) {
        remote.user = "${userName}"
        remote.password = "${password}"
    }
    return remote
}

def getShellPrama(stringshell) {
    shellprama = sh(returnStdout: true, script: stringshell).trim()
    return shellprama
}

def getSSHPrama(ip,stringshell) {
    rserver = GetRemoteServer(ip)
    pwd = rserver.password
    println(pwd)
    sshShell = "sshpass -p "+pwd+" ssh -o StrictHostKeyChecking=no root@${ip} "+stringshell
    println(sshShell)
    sshprama = getShellPrama(sshShell)
    return sshprama
}
