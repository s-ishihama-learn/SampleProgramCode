package main

import (
	"bytes"
	"fmt"

	"golang.org/x/crypto/ssh"
)

// go get golang.org/x/crypto/ssh

func serverSsh(host string, port string, userId string, passwd string, path string) int {
	config := &ssh.ClientConfig{
		User: userId,
		Auth: []ssh.AuthMethod{
			ssh.Password(passwd),
		},
		HostKeyCallback: ssh.InsecureIgnoreHostKey(), // password認証は設定
	}
	address := host + ":" + port
	client, err := ssh.Dial("tcp", address, config)
	if err != nil {
		fmt.Println("Failed to dial: ", err)
		return 1
	}
	defer client.Close()

	session, err := client.NewSession()
	if err != nil {
		fmt.Println("Failed to create session: ", err)
		return 2
	}
	defer session.Close()

	var b bytes.Buffer
	session.Stdout = &b
	if err := session.Run("test -d " + path); err != nil {
		fmt.Println("Failed to run: " + err.Error())
		return 3
	}
	// fmt.Println(b.String())
	return 0
}
