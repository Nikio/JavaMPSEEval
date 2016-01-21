#include "module_traffic.h"

#include <vector>
#include <string>
#include <iostream>
#include <fstream>
#include <ctime>
#include <iostream>
using namespace std;

void Modules::Traffic::Init(storm::MessageData* msg)
{
}


std::string Modules::Traffic::onlyNumeric(std::string input)
{
        std::string res = "";

        for(int i = 0; i < input.size(); i++)
        {
                if(input[i] >= '0' && input[i] <= '9')
                {
                        res += input[i];
                }
        }
        return res;
}

void Modules::Traffic::Run()
{
        std::string cmd = "sh traffic.sh";
        std::string res = Modules::Module::execute(cmd.c_str());

        std::vector<std::string> resParts;
        split(res, ',', resParts);
        try{
                if (resParts.size() < 2) return;
                if(lastIn == -1 || lastOut == -1)
                {
                        lastIn = stol(onlyNumeric(resParts[0]));
                        lastOut = stol(onlyNumeric(resParts[1]));
                        lastSumIn = stol(onlyNumeric(resParts[0]));
                        lastSumOut = stol(onlyNumeric(resParts[1]));
                }
                else
                {
                        lastIn = stol(onlyNumeric(resParts[0])) - lastSumIn;
                        lastOut = stol(onlyNumeric(resParts[1])) - lastSumOut;
                        lastSumIn = stol(onlyNumeric(resParts[0]));
                        lastSumOut = stol(onlyNumeric(resParts[1]));
                }
                error = false;
        } catch(exception& e) {
                error = true;
                cout << "error parsing to two ints: '" << res << "'" << endl;
        }
}

bool Modules::Traffic::SendReply(int newsockfd)
{
        if(error) return false;
        /*
           std::ifstream fl("wlp3s0-minute.png");
           fl.seekg( 0, std::ios::end );
           size_t len = fl.tellg();
           char *ret = new char[len];
           fl.seekg(0, std::ios::beg);
           fl.read(ret, len);
           fl.close();
         */

        //ok, run is complete, send the result back!
        time_t t = time(0); // get time now
        struct tm * now = localtime( &t );
        storm::MessageData* sendmsg = new storm::MessageData();
std: string x = std::to_string(now->tm_hour) + ":" +  std::to_string(now->tm_min) + ":" +  std::to_string(now->tm_sec) + ";" + std::to_string(lastIn) + ";" + std::to_string(lastOut);
        //std::cout << "  " << x << "  " << std::endl << std::flush;
        sendmsg->set_command("storm.traffic");
        sendmsg->set_payload(x);
        //sendmsg->set_payload(ret, len);
        return Module::sendMessage(sendmsg, newsockfd);
}
