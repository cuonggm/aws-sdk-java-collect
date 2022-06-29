# aws-sdk-java-collect

## Git

1. `git rm --cached <file>` : Remove file that was tracked in git
2. `git rm -r --cached <folder>` : Remove folder that was tracked in git
3. `git rebase -i HEAD~<number>` : Merge commits

## Docker

1. `docker pull <image_name>`
2. `docker run -d <image_name>`
3. `docker ps`, `docker ps -a`
4. `docker stop <id>`
5. `docker run -d  -p6000:6379 <image_name>` --name <name> <image_name>
6. `docker logs <name/id>`
7. `docker run -it <image_name>`
8. `docker exec -it <id> /bin/bash` 
9. `docker start --name <name>`
10. `docker-compose -f <file_name>.yaml up`
11. `docker login`
12. `docker tag <image_name:tag> <repo>/<name>:<tag>` 
13. `docker push <repo>/<name>:<tag>`
