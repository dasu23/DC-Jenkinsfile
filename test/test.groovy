import hudson.model.*;

println env.JOB_NAME
println env.BUILD_NUMBER

pipeline{

    agent any
    stages{
        stage("init") {
            steps{
                script{
                    model_test = load env.WORKSPACE + "/test/module.groovy"
                }
            }
        }
        stage("read properties") {
            steps{
                script{
                    properties_file = load env.WORKSPACE + "/test/xxx.properties"
                    model_test.read_properties(properties_file)
                    println "================================"
                }
            }
        }
    }
}

