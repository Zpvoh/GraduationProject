# SpringBoot Restful API后端

数据库采用 Neo4j 图数据库

## 项目生成 jar 包

- 进入项目根目录下，target目录下生成jar包

```
mvn clean package
```

- jar 包上传到服务器上，执行命令，正常运行：

```
java -jar mindmap-backend-0.0.1-SNAPSHOT.jar
```
 
 暂时不考虑用 Docker 容器化运行
 
## Neo4j部署

在Linux系统，$PWD表示当前目录路径


```
git clone https://github.com/mind-edu/neo4j-docker.git
cd neo4j-docker
```
将``neo4j/data``文件夹替换为本项目中data文件夹后，在``neo4j-docker``目录下运行
```
docker run \
    --name testneo4j \
    -p7474:7474 -p7687:7687 \
    -d \
    -v $PWD/neo4j/data:/data \
    -v $PWD/neo4j/logs:/logs \
    -v $PWD/neo4j/import:/var/lib/neo4j/import \
    -v $PWD/neo4j/plugins:/plugins \
    --env NEO4J_AUTH=neo4j/test \
    neo4j:latest
```
data数据持久化的目录: ``$PWD/neo4j/data``

在浏览器上打开``http://0.0.0.0:7474``，用户名为 neo4j，密码为 test，登录

参考资料 :

https://neo4j.com/developer/docker-run-neo4j/
