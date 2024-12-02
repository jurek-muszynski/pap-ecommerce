#!/bin/bash
# This bash script is used to install all necessary dependencies and run the application

# Install docker
install_docker() {
    sudo apt-get update -y
    sudo apt-get install ca-certificates curl -y
    sudo install -m 0755 -d /etc/apt/keyrings
    sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
    sudo chmod a+r /etc/apt/keyrings/docker.asc

    echo \
    "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
    $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update -y

    sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin -y
    sudo groupadd docker
    sudo usermod -aG docker $USER

    curl -SL https://github.com/docker/compose/releases/download/v2.30.3/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
}

# Install openjdk-17
install_java17(){
    sudo apt-get install openjdk-17-jdk -y
    
    sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 1
    sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-17-openjdk-amd64/bin/javac 1
    sudo update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java
    sudo update-alternatives --set javac /usr/lib/jvm/java-17-openjdk-amd64/bin/javac
}

# Install openjfx
install_javafx(){
    sudo apt-get install openjfx -y
    sudo add-apt-repository ppa:kisak/kisak-mesa -y
    sudo apt update -y
    sudo apt upgrade -y
}

# cleanup function, stopping all docker containers after exiting from the application
cleanup() {
    echo "Stopping Docker containers..."
    docker-compose down
}
trap cleanup EXIT

# Check if docker is installed
if [ $(docker --version | wc -l) -eq 0 ]; then
    install_docker
fi

# install openjdk-17
if [ !$(java --version 2>&1 | grep -q "17") ]; then
    install_java17
fi

# install openjfx
if [ $(dpkg -l | grep openjfx | wc -l) -eq 0 ]; then
    install_javafx
fi 

# set the environment variable for the docker-compose secret
export SECRET_FILE_PATH=$(pwd)/secrets/db_password.txt

# run docker-compose
docker-compose up --build -d --always-recreate-deps

# build the frontend application
cd ./frontend
chmod +x ./mvnw
./mvnw clean install

# run the frontend application
APP_NAME=$(ls ./target/frontend-0.0.1.jar)

# dummy timeout to wait for the backend to start
sleep 10

# run the frontend application
java --module-path /usr/share/openjfx/lib/ --add-modules javafx.controls,javafx.fxml -jar $APP_NAME

