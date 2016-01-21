#ifndef PING_H
#define PING_H

#include "module.h"

namespace Modules
{
    class Ping : public Module
    {
    public:
        void Init(storm::MessageData* msg);
        void Run();
        bool SendReply(int newsockfd);
    };
}
#endif // PING_H
