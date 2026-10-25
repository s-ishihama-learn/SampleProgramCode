package main

import (
	"net"
)

func portScan(host string, port string) bool {
	address := host + ":" + port
	_, err := net.Dial("tcp", address)
	if err == nil {
		return true
	} else {
		return false
	}
}
