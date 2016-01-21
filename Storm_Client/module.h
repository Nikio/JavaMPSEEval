#ifndef MODULE_H
#define MODULE_H

#include <vector>
#include <string>
#include <sstream>
#include <utility>
#include <unistd.h>

#include "messagedata.pb.h"

namespace Modules
{
    class Module
    {
    protected:
        //!List of arguments, received from CC for the execution
        std::vector<std::string> arguments;
        //!The returnvalue, which will be returned to CC if required
        std::string resultString = "";

    public:
        //!Init function, which consumes the messagedata
        virtual void Init(storm::MessageData* msg) = 0;
        //!Main function, which contains the "real" code
        virtual void Run() = 0;
        //!Function to send the reply
        virtual bool SendReply(int newsockfd) = 0;
        //!getter for the resultString
        std::string GetResultString() { return resultString; }
        //!Setter/Adder for the argument list
        void AddArgument(std::string arg) {arguments.push_back(arg); }

    protected:
        //!sends a message back to the host, given through the handle
        bool sendMessage(storm::MessageData* sendmsg, int sockfd);
        //!function to execute code. implemented here to be used in inherited classes
        std::string execute(const char* cmd);
        //!function to split string. implemented here to be used in inherited classes
        void split(const std::string &s, char delim, std::vector<std::string> &elems);
    };
}
#endif // MODULE_H
