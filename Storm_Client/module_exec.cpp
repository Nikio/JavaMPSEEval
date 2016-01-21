#include "module_exec.h"

#include <vector>
#include <string>
#include <iostream>

void Modules::Exec::Init(storm::MessageData* msg)
{
    AddArgument(msg->commandarguments(0));
    if(msg->stormarguments_size() > 0)
    {
        Silent = false;
    }
}

void Modules::Exec::Run()
{
    //executes the command stored in the 1st argument
    resultString = execute(arguments[0].c_str());
}

bool Modules::Exec::SendReply(int newsockfd)
{
    storm::MessageData* sendmsg = new storm::MessageData();
    sendmsg->set_command("execute.result");
    sendmsg->set_payload(GetResultString());
    return Module::sendMessage(sendmsg, newsockfd);
}
