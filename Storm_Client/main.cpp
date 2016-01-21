#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <vector>
#include <sstream>
#include <utility>
#include <iostream>
#include <fstream>
#include <pthread.h>
#include <sys/types.h>
#include <signal.h>


#include "module_slowhttptest.h"
#include "module_hping3.h"
#include "module_ping.h"
#include "module_exec.h"
#include "module_traffic.h"
#include "messagedata.pb.h"

//! Splits a string after each "delim"-char
/*!
   \param s input string
   \param delim separation char
   \param elems return the list of strings
 */
void split(const std::string &s, char delim, std::vector<std::string> &elems) {
        std::stringstream ss(s);
        std::string item;
        int i = 0;
        while (std::getline(ss, item, delim)) {
                if(i == 0)
                        elems.push_back(item);
                else
                        elems.push_back(item);
                i++;
        }
}


//! equivalent of throw&exit
/*!
   \param msg message strings to be shown before exit
 */
void error(std::string msg)
{
        perror(msg.c_str());
        exit(1);
}

void* sendTraffic(void* newsockfd)
{
        Modules::Traffic* t = new Modules::Traffic;
        while(true)
        {
                t->Run();
                t->SendReply((long)newsockfd);
                sleep(1);
        }

}


int main(int argc, char *argv[])
{
        std::cout << "   ______                   ________          __ \n  / __/ /____  ______ _    / ___/ (_)__ ___  / /_\n _\\ \\/ __/ _ \\/ __/  ' \\  / /__/ / / -_) _ \\/ __/\n/___/\\__/\\___/_/ /_/_/_/  \\___/_/_/\\__/_//_/\\__/ \n" << std::endl;

        //init RND
        /*
           Modules::Traffic* t = new Modules::Traffic;
           Modules::Exec *pre = new Modules::Exec;
           pre->AddArgument("rrdtool create wlp3s0.rrd -s 1 DS:in:DERIVE:10:0:U DS:out:DERIVE:10:0:U RRA:AVERAGE:0.5:1:1200 RRA:AVERAGE:0.5:6:7200");
           pre->Run();
           pthread_t gatherThread;
           int  gatherThreadReturn;
           gatherThreadReturn = pthread_create( &gatherThread, NULL, gatherTraffic, NULL);
           if(gatherThreadReturn)
           {
            fprintf(stderr,"Error - pthread_create() return code: %d\n",gatherThreadReturn);
            exit(EXIT_FAILURE);
           }*/

        bool inMajorloop = true;
        int sockfd, newsockfd, portno;
        //socket stuff! :D
        socklen_t clilen;
        char incomeBuffer[65536];
        struct sockaddr_in serv_addr, cli_addr;
        //no port number given as argument? use 9000!
        if (argc < 2) {
                portno = 9000;
        }
        else
        {
                portno = atoi(argv[1]);
        }
        //open socket
        sockfd = socket(AF_INET, SOCK_STREAM, 0);
        if (sockfd < 0)
                error("ERROR opening socket");
        bzero((char *) &serv_addr, sizeof(serv_addr));
        serv_addr.sin_family = AF_INET;
        serv_addr.sin_addr.s_addr = INADDR_ANY;
        serv_addr.sin_port = htons(portno);
        if (bind(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)
                error("ERROR on binding");
        listen(sockfd,5);
        clilen = sizeof(cli_addr);

        while(inMajorloop)
        {
                std::cout << "\n\nWait for Connection\n" << std::endl;
                bool inloop = true;
                //wait for connection
                newsockfd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);
                if (newsockfd < 0)
                        error("ERROR on accept");
                std::cout << "\n\nConnection established\n" << std::endl;

                //END SOCKET CREATION HERE, lets go do stuff

                pthread_t trafficSendThread;
                int trafficSendThreadReturn;
                trafficSendThreadReturn = pthread_create( &trafficSendThread, NULL, sendTraffic, (void *) (intptr_t) newsockfd);
                if(trafficSendThreadReturn)
                {
                        fprintf(stderr,"Error - pthread_create() return code: %d\n",trafficSendThreadReturn);
                        exit(EXIT_FAILURE);
                }


                int packageSizeReceived;
                int packageSize;
                pid_t childPid = 0;
                int n = 0;
                //run loop, goes forever and ever, until we are commanded to shut down :(
                while(inloop)
                {
                        //read new package
                        packageSizeReceived = 0;
                        packageSize = -1;
                        char* msgBuffer;
                        //loop, if message is bigger then the 64k buffer
                        while(true)
                        {
                                bzero(incomeBuffer,65536); //zero out buffer
                                n = read(newsockfd,incomeBuffer,65536); //read the socket, copy it into buffer (count of n)

                                if (n <= 0)
                                {
                                        inloop = false;
                                        break;
                                }
                                //error("ERROR reading from socket");

                                //n can be 0 for "noop"-messages
                                if (n > 0)
                                {
                                        //is it the 1st package?
                                        if(packageSize == -1)
                                        {
                                                //how big will the msg be?
                                                memcpy(&packageSize, &incomeBuffer[0], 4);
                                                msgBuffer = new char[packageSize+4];
                                        }
                                        //copy it into msgbuffer (size = size of complete msg)
                                        memcpy(&msgBuffer[packageSizeReceived], &incomeBuffer[0], n);
                                        packageSizeReceived += n;
                                        //is it complete?
                                        if(packageSizeReceived - 4 >= packageSize)
                                                break;
                                }
                        }
                        if(n <= 0)
                        {
                                printf("disconnected :(");
                                pthread_cancel(trafficSendThread);
                                if(childPid != 0)
                                {
                                        kill (childPid, 9);
                                        childPid = 0;
                                }
                                sleep(2);
                        }
                        else if(n > 4)
                        {
                                storm::MessageData* msg = new storm::MessageData();
                                printf("Got %i bytes (packageSize)\n", packageSize);
                                //parse msg into struct
                                msg->ParseFromArray(&msgBuffer[4], packageSize);
                                //delete(msgBuffer);

                                //msg was parsed, what do we have to do?
                                printf("Command -> %s\n", msg->command().c_str());

                                //split the command into its parts
                                std::vector<std::string> parts;
                                split(msg->command(), '.', parts);

                                //"else if"-decition tree -> what to do: like a switch-case, but i dont like switch-cases for complex code
                                if(strcmp("quit", msg->command().c_str()) == 0)
                                {
                                        inloop = false; //quit! :(
                                        inMajorloop = false; //quit! :(
                                        if(childPid != 0)
                                                kill (childPid, 9);
                                }
                                if(strcmp("stop", msg->command().c_str()) == 0)
                                {
                                        if(childPid != 0)
                                        {
                                                kill (childPid, 9);
                                                childPid = 0;
                                        }
                                }
                                else if(strcmp(parts[0].c_str(), "ping") == 0 || strcmp("ping", msg->command().c_str()) == 0)
                                {
                                        if(childPid == 0)
                                        {
                                                childPid = fork();
                                                //new module ping
                                                if (childPid == 0) {//child
                                                        Modules::Ping *p = new Modules::Ping;
                                                        p->Init(msg);

                                                        p->Run();
                                                        {
                                                                p->SendReply(newsockfd);
                                                        }
                                                }
                                        }
                                }
                                else if (strcmp(parts[0].c_str(), "hping3") == 0 || strcmp("hping3", msg->command().c_str()) == 0)
                                {
                                        if (childPid == 0)
                                        {
                                                childPid = fork();
                                                //new module ping
                                                if (childPid == 0) {//child
                                                        Modules::Hping3 *p3 = new Modules::Hping3;
                                                        p3->Init(msg);

                                                        p3->Run();
                                                        {
                                                                p3->SendReply(newsockfd);
                                                        }
                                                }
                                        }
                                }
                                else if (strcmp(parts[0].c_str(), "showhttptest") == 0 || strcmp("showhttptest", msg->command().c_str()) == 0)
                                {
                                        if (childPid == 0)
                                        {
                                                childPid = fork();
                                                //new module ping
                                                if (childPid == 0) {//child
                                                        Modules::SlowHttpTest *s = new Modules::SlowHttpTest;
                                                        s->Init(msg);

                                                        s->Run();
                                                        {
                                                                s->SendReply(newsockfd);
                                                        }
                                                }
                                        }
                                }
                                else if(strcmp(parts[0].c_str(), "storm") == 0)
                                {
                                        if(strcmp(parts[1].c_str(), "filetransfer") == 0)
                                        {
                                                if(msg->commandarguments_size() < 1)
                                                        error("no filename given");

                                                //the bytearray is saved into a string, dont be fooled, its no real string!
                                                std::string file = msg->payload();

                                                std::ofstream out;

                                                //write it to disk
                                                out.open (msg->commandarguments(0), std::ios::out | std::ios::app | std::ios::binary);
                                                out << file;
                                                out.close();
                                        }
                                        else if(strcmp(parts[1].c_str(), "execute") == 0)
                                        {
                                                Modules::Exec *e = new Modules::Exec;
                                                e->Init(msg);

                                                if(!e->Silent)
                                                {
                                                        e->Run();
                                                        {
                                                                e->SendReply(newsockfd);
                                                        }
                                                }
                                                else if(childPid == 0)
                                                {
                                                        childPid = fork();
                                                        //new module ping
                                                        if (childPid == 0) {//child
                                                                e->Run();
                                                                {
                                                                        e->SendReply(newsockfd);
                                                                }
                                                        }
                                                }
                                        }
                                }
                                std::cout << "- - - - \n";
                                //delete(msg);
                        }
                }
        }
        close(newsockfd);
        close(sockfd);
        return 0;
}
