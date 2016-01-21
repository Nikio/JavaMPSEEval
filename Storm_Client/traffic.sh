var1=$( cat /proc/net/dev | grep wlp3s0 | tr -s " " | cut -d" " -f2 );
var2=$( cat /proc/net/dev | grep wlp3s0 | tr -s " " | cut -d" " -f11 );
printf "%s,%s" $var1 $var2
