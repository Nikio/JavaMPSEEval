#include "module.h"
#include <stdio.h>
#include <iostream>

//! Executes a console command on a console and WHEN IT IS FINISHED returns the output from the console.
/*!
 \param cmd string of the command
 \return output string
 */
std::string Modules::Module::execute(const char* cmd) {
    FILE* pipe = popen(cmd, "r");
    if (!pipe) return "ERROR";
    char buffer[128];
    std::string result = "";
    while (!feof(pipe)) {
        if (fgets(buffer, 128, pipe) != NULL)
            result += buffer;
    }
    pclose(pipe);
    return result;
}


//! Splits a string after each "delim"-char
/*!
 \param s input string
 \param delim separation char
 \param elems return the list of strings
 */
void Modules::Module::split(const std::string &s, char delim, std::vector<std::string> &elems) {
    std::stringstream ss(s);
    std::string item;
    int i = 0;
    while (std::getline(ss, item, delim)) {
        if(i == 0)
            elems.push_back(item);
        else
            elems.push_back(item.erase(0,1));
        i++;
    }
}

bool Modules::Module::sendMessage(storm::MessageData* sendmsg, int sockfd)
{
    int size = sendmsg->ByteSize();
    char sendbuf[size + 4];

    //serialize and set the size as prefix
    sendmsg->SerializeToArray(&sendbuf[4], size);
    memcpy(&sendbuf[0], &size, 4);

    //and send it!
    int n = write(sockfd, sendbuf, size + 4);
    std::cout << "written bytes from " <<  sendmsg->command().c_str() << " with " << size << " bytes" << std::endl << std::flush;
    if (n < 0)
        return false;
    return true;
}
