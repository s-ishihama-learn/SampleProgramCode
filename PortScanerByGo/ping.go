package main

import (
	"github.com/paulstuart/ping"
)

// go get github.com/paulstuart/ping

func serverPing(host string, count int) bool {
	alive := ping.Ping(host, count)
	return alive
}
