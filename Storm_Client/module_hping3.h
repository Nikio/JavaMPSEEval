#ifndef MODULE_HPING3_H
#define MODULE_HPING3_H

#include "module.h"

namespace Modules
{
    class Hping3 : public Module
    {
    public:
        void Init(storm::MessageData* msg);
        void Run();
        bool SendReply(int newsockfd);
    };
}
#endif // HPING3_H
