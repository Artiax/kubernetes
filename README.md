# Kubernetes (Minikube)
- Consul
- Vault
- Jenkins (master with dynamic kubernetes slaves for image build pipeline).
- Tiller (helm)

## Minikube
For instructions on how to setup Minikube on your machine refer to [Install Minikube](https://kubernetes.io/docs/tasks/tools/install-minikube/).
```sh
# Start minikube (the following configuration is what this project has been built/tested on)
$ minikube start \
  --kubernetes-version v1.9.8 \ --extra-config=apiserver.admission-control="Initializers,NamespaceLifecycle,LimitRanger,ServiceAccount,DefaultStorageClass,ValidatingAdmissionWebhook,ResourceQuota" \
  --extra-config=apiserver.authorization-mode="RBAC"
```

## Consul
```sh
# Create a persistent volume to retain the data when container is restarted.
$ kubectl apply -f persistentVolumeClaims/consul.yaml
# Create a service and deployment for Consul.
$ kubectl apply -f services/consul.yaml -f deployments/consul.yaml

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

## Jenkins
```sh
# Clone required repositories.
$ git clone https://github.com/Artiax/jenkins.git
$ git clone https://github.com/Artiax/jenkins-slave.git

# Build Jenkins master and slave images.
$ eval $(minikube docker-env)
$ docker build -t jenkins:0.0.1 -t jenkins:latest jenkins
$ docker build -t jenkins-slave:0.0.1 -t jenkins-slave:latest jenkins-slave

# Create a persistent volume to retain the jobs when container is restarted.
$ kubectl apply -f persistentVolumeClaims/jenkins.yaml
# Create a service and deployment for Jenkins.
$ kubectl apply -f services/jenkins.yaml -f deployments/jenkins.yaml

# Access Jenkins UI.
$ minikube service jenkins
```

## Tiller
```sh
# Create a service and deployment for Tiller.
$ kubectl apply -f services/tiller.yaml -f deployments/tiller.yaml

# Access Helm (we're having to use cut to remove http:// protocol as --format in minikube is currently broken)
$ export HELM_HOST=$(minikube service tiller -n kube-system --url | cut -d '/' -f3)
$ helm version
```
