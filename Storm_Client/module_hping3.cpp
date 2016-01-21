#include "module_hping3.h"

#include <vector>
#include <string>
#include <iostream>

void Modules::Hping3::Init(storm::MessageData* msg)
{
    for(int i = 0; i < msg->commandarguments_size(); i++)
    {
        AddArgument(msg->commandarguments(i)); //add all arguments given and give it to the module
    }
}

void Modules::Hping3::Run()
{
    std::string cmd = "hping3 ";
    for(unsigned int i = 0; i < arguments.size(); i++)
    {
        cmd += arguments[i] + " ";
    }

    printf("hping3 cmd: %s", cmd.c_str());
    //executes the Hping3 (+args)
    std::string res = Modules::Module::execute(cmd.c_str());

    //split the result, i only need the time
    std::vector<std::string> resParts;
    split(res, '=', resParts);
    int l = resParts.size();
    resultString = resParts[l-1];
}

bool Modules::Hping3::SendReply(int newsockfd)
{
    //ok, run is complete, send the result back!
    storm::MessageData* sendmsg = new storm::MessageData();
    sendmsg->set_command("hping3.result");
    sendmsg->set_payload(GetResultString());

    int size = sendmsg->ByteSize();
    char sendbuf[size + 4];

    //serialize and set the size as prefix
    sendmsg->SerializeToArray(&sendbuf[4], size);
    memcpy(&sendbuf[0], &size, 4);

    //and send it!
    int n = write(newsockfd, sendbuf, size + 4);
    if (n < 0)
        return false;
    delete(sendmsg);
    return true;
}
