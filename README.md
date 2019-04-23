# Kubernetes (Minikube)
- [Minikube](#minikube)
- [Consul](#consul)
- [Vault](#vault)
- [OpenLDAP](#openldap)
- [phpLDAPadmin](#phpldapadmin)
- [Jenkins](#jenkins) (master with dynamic kubernetes slaves for image build pipeline)
- [Tiller](#tiller) (helm)

## Minikube
For instructions on how to setup Minikube on your machine refer to [Install Minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/).
```sh
# Start minikube (the following configuration is what this project has been built/tested on)
$ minikube start \
  --kubernetes-version v1.11.8 \
  --extra-config=apiserver.enable-admission-plugins="Initializers,NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,ResourceQuota" \
  --extra-config=apiserver.authorization-mode="RBAC"
```

## Consul
```sh
# Create a persistent volume to retain the data when container is restarted.
$ kubectl apply -f persistentVolumeClaims/consul.yaml
# Create a service and deployment for Consul.
$ kubectl apply -f services/consul.yaml -f deployments/consul.yaml
# Populate jenkins/rbac Consul key value with preliminary rbac configuration.
$ curl --data-binary @consul/rbac -X PUT "$(minikube service consul --url)/v1/kv/jenkins/rbac"

# Access Consul UI
$ minikube service consul
```

## Vault
```sh
# Create a persistent volume to retain the data when container is restarted.
$ kubectl apply -f persistentVolumeClaims/vault.yaml
# Create a service and deployment for Vault.
$ kubectl apply -f services/vault.yaml -f deployments/vault.yaml

# Initialize Vault (save the unseal keys and initial root token)
$ export VAULT_ADDR=$(minikube service vault --url)
$ vault init

# Unseal Vault (repeat this step every time container is restarted)
$ export VAULT_ADDR=$(minikube service vault --url)
$ vault unseal [Unseal Key 1]
$ vault unseal [Unseal Key 2]
$ vault unseal [Unseal Key 3]

# Access Vault.
$ export VAULT_ADDR=$(minikube service vault --url)
$ export VAULT_TOKEN='[Initial Root Token]'
$ vault status
```

## OpenLDAP
```sh
# Create a persistent volume to retain the data when container is restarted.
$ kubectl apply -f persistentVolumeClaims/openldap.yaml
# Create a service and deployment for OpenLDAP.
$ kubectl apply -f services/openldap.yaml -f deployments/openldap.yaml
```

## phpLDAPadmin
```sh
# Create a service and deployment for phpLDAPadmin.
$ kubectl apply -f services/phpldapadmin.yaml -f deployments/phpldapadmin.yaml

# Access phpLDAPadmin UI.
$ minikube service phpldapadmin --https
  Password: admin
```

## Jenkins
```sh
# Clone required repositories.
$ git clone https://github.com/Artiax/jenkins.git
$ git clone https://github.com/Artiax/jenkins-slave.git

# Build Jenkins master and slave images.
$ eval $(minikube docker-env)
$ docker build -t jenkins:0.0.1 -t jenkins:latest jenkins
$ docker build -t jenkins-slave:0.0.1 -t jenkins-slave:latest jenkins-slave

# Create a configMap with Consul and Vault addresses.
$ kubectl create configmap jenkins --from-literal=CONSUL_ADDR=$(minikube service consul --url) --from-literal=VAULT_ADDR=$(minikube service vault --url)
# Create a persistent volume to retain the jobs when container is restarted.
$ kubectl apply -f persistentVolumeClaims/jenkins.yaml
# Create a service account and relevant policies required for slave provisioning.
$ kubectl apply -f serviceAccounts/jenkins.yaml -f clusterRoles/jenkins.yaml -f roleBindings/jenkins.yaml
# Create a service and deployment for Jenkins.
$ kubectl apply -f services/jenkins.yaml -f deployments/jenkins.yaml

# Use the following URL to create first admin user.
$ echo $(minikube service jenkins --url | head -1)/securityRealm/firstUser

# Access Jenkins UI.
$ minikube service jenkins
```

## Tiller
```sh
# Create a service and deployment for Tiller.
$ kubectl apply -f services/tiller.yaml -f deployments/tiller.yaml

# Access Helm
$ export HELM_HOST=$(minikube service tiller -n kube-system --url --format "{{.IP}}:{{.Port}}")
$ helm version
```
