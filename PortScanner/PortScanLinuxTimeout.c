/*
 * PortScanLinuxTimeout.c
 *
 *  Created on: 2011/11/25
 *      Author: ishihama
 *
 *  (Linux) gcc portscan.c portscanlinux.c -o portscan.exe
 *  (Solaris) gcc -lsocket -lnsl portscan.c portscanlinux.c -o portscan.exe
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/socket.h>
#include <netdb.h>
#include <netinet/in.h>

/*
 * ポートスキャン
 * *address : IPアドレス
 * port : ポート番号
 * return : 0:使用 1:未使用 -1:ソケットエラー
 */
int PortScan(char *address, unsigned short port, long time_out) {
	int dstSocket;
	struct sockaddr_in dstAddr;
	int rc, flag;
	struct timeval tv;
	fd_set fdchk;
	int ret;

	/* sockaddr_in 構造体のセット */
	memset(&dstAddr, 0, sizeof(dstAddr));
	dstAddr.sin_port = htons(port);
	dstAddr.sin_family = AF_INET;
	dstAddr.sin_addr.s_addr = inet_addr(address);

	/* タイムアウト設定 */
	tv.tv_sec = time_out;
	tv.tv_usec = 0;

	/* ソケット生成 */
	dstSocket = socket(AF_INET, SOCK_STREAM, 0);
	if (dstSocket < 0) {
		rc = -1; /* ソケットエラー */

	} else {
		//ノンブロックに変更
		flag = fcntl(dstSocket, F_GETFL, 0);
		if (flag < 0) {
			rc = -1;
		} else if (fcntl(dstSocket, F_SETFL, flag | O_NONBLOCK) < 0) {
			rc = -1;

		} else {
			/* 接続 */
			if (connect(dstSocket, (struct sockaddr *) &dstAddr, sizeof(dstAddr)) < 0) {
				if (errno == EINPROGRESS) {
					FD_ZERO(&fdchk);
					FD_SET(dstSocket, &fdchk);
					ret = select(dstSocket + 1, NULL, &fdchk, NULL, &tv);
					if (ret <= 0) {
						rc = 1; /* 未使用 */
					} else {
						rc = 0; /* 使用 */
					}
				} else {
					rc = 1; /* 未使用 */
				}
			} else {
				rc = 0; /* 使用 */
			}
		}
	}

	/* ソケット終了 */
	close(dstSocket);

	return rc;
}
