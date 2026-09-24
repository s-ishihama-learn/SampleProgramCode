/*
 * PortScanWin.c
 *
 *  Created on: 2011/11/25
 *      Author: ishihama
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <winsock2.h>
#include <ws2tcpip.h>


/*
 * ポートスキャン
 * *address : IPアドレス
 * port : ポート番号
 * return : 0:使用 1:未使用 -1:ソケットエラー
 */
int PortScan(char *address, unsigned short port, long time_out) {
	int dstSocket;
	struct sockaddr_in dstAddr;
	int rc;

	/* Windows 独自の設定 */
	WSADATA data;
	WSAStartup(MAKEWORD(2,0), &data);

	/* sockaddr_in 構造体のセット */
	memset(&dstAddr, 0, sizeof(dstAddr));
	dstAddr.sin_port = htons(port);
	dstAddr.sin_family = AF_INET;
	dstAddr.sin_addr.s_addr = inet_addr(address);

	/* ソケット生成 */
	dstSocket = socket(AF_INET, SOCK_STREAM, 0);
	if (dstSocket < 0) {
		rc = -1; /* ソケットエラー */

	} else {
		/* 接続 */
		if (connect(dstSocket, (struct sockaddr *) &dstAddr, sizeof(dstAddr)) < 0) {
			rc = 1; /* 未使用 */
		} else {
			rc = 0; /* 使用 */
		}
	}

	/* Windows 独自の設定 */
	closesocket(dstSocket);
	WSACleanup();

	return rc;
}
