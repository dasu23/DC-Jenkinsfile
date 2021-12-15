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