#! /usr/bin/env python
import sys

from scapy.all import *

print (sys.argv, len(sys.argv))
destIP = sys.argv[1]
interface = sys.argv[2]
msgType = sys.argv[3]
msg = sys.argv[4]

if msgType == '0':
    packet = IP(dst=destIP)/ ICMP()
elif msgType == '1':
    packet = IP(dst=destIP)/ TCP(dport=80)
elif msgType == '2':
    packet = IP(dst=destIP)/ UDP(dport=53)
else:
    print ("invalid type")
    exit()

count = 0
binary = lambda n: '' if n==0 else binary(n/2) + str(n%2)
for x in msg:
    v = ord(x)
    hb = v << 8
    id = hb | 132
    packet.id = id
    packet.frag = 0 << 8 | count
    count = count + 1
    send(packet,iface=interface)

packet.frag = 1 << 12 | count
send(packet,iface=interface)
print("sent all packets")