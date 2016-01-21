#ifndef EXEC_H
#define EXEC_H

#include "module.h"

namespace Modules
{
    class Exec : public Module
    {
    public:
        void Init(storm::MessageData* msg);
        void Run();
        bool SendReply(int newsockfd);
        bool Silent = true;
    };
}
#endif // PING_H
