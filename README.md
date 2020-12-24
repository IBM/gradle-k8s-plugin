# gradle-kubernetes-plugin

deploy ur java/spring boot project without kubectl

this project will help u doing these steps:

- create default docker image based on https://github.com/bmuschko/gradle-docker-plugin , which has already added in the dependency of this project
- create default service and deployment yaml
- deploy to ur k8s

just config ur project like this:

```
kubernetes {

    namespace = 'namespace' // default 'default'

    ibmcloud {
        apiKey = "${ibmcloud_apikey}"
        clusterId = "${ibmcloud_cluster_id}"
    }

    configFilePath = ''

    kubernetesSpringBootApplication {
            appName = '' // default ${project.name}
            springBootActiveProfiles = '' // default ''
            namespace = '' // default 'default'

            deployment {
                default {   
                    image = '' // default dockerfile.images[0], will skip dockerPushImage if provided
                    replicas = 1 // default 1
                    envs = [
                        'name1': 'value', 
                        'name2': [valueFrom: 'secret', name: 'secretName', key: 'secretKey'], 
                        'name3': [valueFrom: 'configMap', name: 'configMapName', key: 'configMapKey']
                    ] // default empty
                    volumeMounts = ['volumeName1': 'volumeMountPath1', 'volumeName2': 'volumeMountPath2'] // default empty     
                }
                sidecars { }
                volumes = [
                        'volumeName1': [volumeFrom : 'secret', name : 'secretName'],
                        'volumeName2': [volumeFrom : 'configMap',name : 'configMapName'],
                ] // default empty   
            }
    
            service {
                servicePorts = [prot: tergetPort] // default [80: 8080] tcp by default
            }

            ingress {}
    
        }
}

```

## todo

- add more api

```
configMap {}
            
secret {}     

pv{}

serviceAccount {}

role {}

clusetRole {}

roleBinding {}

clusterRoleBinding {}
```

- separate deploy process for each api，combine any api u needs
- support helm charts
