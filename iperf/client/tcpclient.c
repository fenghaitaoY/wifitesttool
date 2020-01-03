/*************************************************************************
	> File Name: tcpclient.c
	> Author: fht
	> Mail: htfeng@sci-inc.com.cn 
	> Created Time: 2019年12月31日 星期二 15时23分15秒
 ************************************************************************/

#include<stdio.h>
#include<sys/socket.h>
#include<sys/un.h>

#define SOCKET_PATH "/tmp/mysocket"
int main()
{
    int sockfd = -1;
    int addrlen = 0;
    struct sockaddr_un addr;

    sockfd = socket(PF_UNIX, SOCK_STREAM, 0);
    if (sockfd < 0) {
        fprintf(stderr, "Can not create socket for twin!\n");
        return -1;
    }

    bzero(&addr, sizeof(addr));
    strcpy(&addr.sun_path[1], SOCKET_PATH);
    addr.sun_family = AF_UNIX;

    addrlen = 1 + strlen(SOCKET_PATH) + sizeof(addr.sun_family);
    if (connect(sockfd, (struct sockaddr *)&addr, addrlen) < 0) {
        fprintf(stderr, "Can not connect server!\n");
        close(sockfd);
        return -1;
    }
    if((send(sockfd, "da sha zi", strlen("da sha zi"), 0)) == -1){
        perror("send");
        exit(1);
    }
    close(sockfd);

    return 0;
}
