package com.dynatrace.ace

/***************************\
  This function assumes we run on a Jenkins Agent that has kubectl available.
  It waits for the deployment to be ready
  Returns either 0(=no errors), 1(=time out waiting)
\***************************/

def waitForDeployment( Map args ) 
    
    /*  String deploymentName
    String environment
    int iterations
    int timeout*/
{
    // check input arguments
    String deploymentName = args.containsKey("deploymentName") ? args.deploymentName : ""
    String environment = args.containsKey("environment") ? args.environment : ""
    int iterations = args.containsKey("iterations") ? args.iterations : 30
    int timeout = args.containsKey("timeout") ? args.timeout : 10

    // check minimum required params
    if(deploymentName == "" || environment == "") {
        echo "deploymentName and environment are mandatory parameters!"
        return -1
    }

    String result="1"
    int i=0
    while (result!="0" && i < iterations) {
        //command will return status code 1 if deployment is not ready
        result=sh(script: "kubectl -n ${environment} rollout status deployment ${deploymentName} --timeout=${timeout}s", returnStatus: true)
        i++
    }

    // Wait for pod to start accepting HTTP requests
    sleep (10)

    return result.isInteger() ? result.toInteger() : 1
}

return this