#ifndef MODULE_TRAFFIC_H
#define MODULE_TRAFFIC_H


#include "module.h"

namespace Modules
{
    class Traffic : public Module
    {
    public:
        void Init(storm::MessageData* msg);
        void Run();
        bool SendReply(int newsockfd);
    private:
        long lastIn = -1;
        long lastOut = -1;
        long lastSumIn = -1;
        long lastSumOut = -1;
        bool error = true;
        std::string onlyNumeric(std::string input);
    };
}

#endif // MODULE_TRAFFIC_H
