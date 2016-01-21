#include "module_ping.h"

#include <vector>
#include <string>
#include <iostream>

void Modules::Ping::Init(storm::MessageData* msg)
{
    for(int i = 0; i < msg->commandarguments_size(); i++)
    {
        AddArgument(msg->commandarguments(i)); //add all arguments given and give it to the module
    }
}

void Modules::Ping::Run()
{
    std::string cmd = "ping ";
    for(unsigned int i = 0; i < arguments.size(); i++)
    {
        cmd += arguments[i] + " ";
    }
    //executes the ping (+args)
    std::string res = Modules::Module::execute(cmd.c_str());

    //split the result, i only need the time
    std::vector<std::string> resParts;
    split(res, '=', resParts);
    int l = resParts.size();
    resultString = resParts[l-1];
}

bool Modules::Ping::SendReply(int newsockfd)
{
    //ok, run is complete, send the result back!
    storm::MessageData* sendmsg = new storm::MessageData();
    sendmsg->set_command("ping.result");
    sendmsg->set_payload(GetResultString());
    return Module::sendMessage(sendmsg, newsockfd);
}
