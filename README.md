# LLM-Database-Chatbot-WebProgramming-Project
My project for the Web Programming subject at my uni.

# How to Start?
- Download Docker
- Run "docker compose up" in the Root folder

# Enviroment Variables
Set the "HUGGINGFACE_API_TOKEN" in the "docker-compose.yml" file to a personal HuggingFace API token to set to the default Admin User.
Here are the default Admin User credentials:
- Username: deannaste123
- Password: 1234

# Additional Information
The Admin user has the testing database "Northwind" included.

# K3D Cluster
Now you can also deploy this web application on a Kubernetes K3D cluster using the manifests in the "k3d_cluster_yaml_files".

If you want to use a different cluster, make sure you get the "Traefik" Ingress and the "local-path" StorageClass that come predownloaded with a K3D cluster.

You'll need to get the K3D cluster, and start it up with the following command:

*'k3d cluster create myCluster -p "80:80@loadbalancer" -s 3 -a 5'*

The cluster takes approx. 20GB of storage, so be careful!
