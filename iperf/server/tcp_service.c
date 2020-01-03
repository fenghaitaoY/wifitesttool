/*************************************************************************
     File Name: tcp_service.c
     Author: fht
     Mail: fht@microarray.com.cn
     Created Time: 2017年05月08日 星期一 22时17分25秒
 ************************************************************************/

#include<stdio.h>
#include<stdlib.h>
#include<errno.h>
#include<string.h>
#include<netinet/in.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<sys/wait.h>
#include <arpa/inet.h>

#define MYPORT 3490
#define BACKLOG 10

int main(int argc, char * argv[]){
    int sockfd,new_fd;
    struct sockaddr_in my_addr;
    struct sockaddr_in their_addr;
    int sin_size;
    char buf[100];

    if((sockfd = socket(AF_UNIX, SOCK_STREAM, 0)) == -1){
        perror("socket");
        exit(1);
    }
    my_addr.sin_family = AF_UNIX; //host byte order
    my_addr.sin_port = htons(MYPORT); // short, network byte order
    my_addr.sin_addr.s_addr = INADDR_ANY; //auto-fill with my IP
    bzero(&(my_addr.sin_zero),sizeof(struct sockaddr_in)); // zero the reset of the struct

    if(bind(sockfd, (struct sockaddr *)&my_addr,sizeof(struct sockaddr)) == -1){
        perror("bind");
        exit(1);
    }

    if(listen(sockfd, BACKLOG) ==-1){
        perror("listen");
        exit(1);
    }

    while(1){//main accept loop
        sin_size = sizeof(struct sockaddr_in);
        if((new_fd= accept(sockfd, (struct sockaddr *)&their_addr,&sin_size)) == -1){
            perror("accept");
            continue;
        }
        printf("server: got connection from %s\n", inet_ntoa(their_addr.sin_addr));

        if(!fork()){ // child process is return 0
            if(recv(new_fd, buf, 100, 0) == -1) perror("recv");
            printf(" recv = %s\n", buf);
            if(send(new_fd, "Hello, world!\n", 14, 0) == -1) perror("send");
            close(new_fd);
            exit(0);
        }
        close(new_fd); // parent doesnot need this
        while(waitpid(-1, NULL, WNOHANG) > 0); // clean up child process
    }
    return 0;
}
