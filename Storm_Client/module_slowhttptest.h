#ifndef SLOWHTTPTEST_H
#define SLOWHTTPTEST_H

#include "module.h"

namespace Modules
{
    class SlowHttpTest : public Module
    {
    public:
        void Init(storm::MessageData* msg);
        void Run();
        bool SendReply(int newsockfd);
    };
}
#endif // SLOWHTTPTEST_H
