/*
 ============================================================================
 Name        : PortScan.c
 Author      :
 Version     :
 Copyright   : Your copyright notice
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
 * IPアドレス形式のチェック（数値形式のみ許可）
 * *ip : IPアドレス文字列
 * return : 0:OK -1:NG
 */
int CheckIpAddr(char *ip) {
	int i, j, k, v;
	char buf[10];

	j = 0;
	k = 0;
	for (i = 0; i < strlen(ip); i++) {
		if (ip[i] == '.') {
			buf[j] = '\0';
			v = atoi(buf);
			if (v < 1 && v > 255) {
				return -1;
			}
			j = 0;
			k++;
		} else {
			if (isdigit(ip[i]) == 0) {
				return -1;
			}
			buf[j] = ip[i];
			j++;
		}
	}
	if (k != 3) {
		return -1;
	}
	return 0;
}

/*
 * IPアドレスとポート番号に分割する
 * *str : IPアドレスとポート番号文字列(xxx.xxx.xxx.xxx:yy)
 * *ip : IPアドレス(output)
 * *port : ポート番号(output)
 */
int GetIpPort(char *str, char *ip, unsigned short *port) {
	int i, j, k, n;
	char buf[16];

	ip[0] = '\0';
	buf[0] = '\0';
	*port = 0;

	n = strlen(str);
	if (n > 24) {
		return -1;
	}

	/* IPアドレスとポート番号の分割 */
	k = -1;
	for (i = n - 1; i >= 0; i--) {
		if (str[i] == ':') {
			k = i;
			break;
		}
	}
	if (i < 0) {
		return -1;
	}

	/* IPアドレス */
	for (i = 0; i < k; i++) {
		ip[i] = str[i];
	}
	ip[i] = '\0';
	if (CheckIpAddr(ip) < 0) {
		return -1;
	}

	/* ポート番号 */
	j = 0;
	for (i = k + 1; i < n; i++) {
		buf[j++] = str[i];
	}
	buf[j] = '\0';
	*port = (unsigned short) atoi(buf);
	if (*port == 0) {
		return -1;
	}

	return 0;
}

int main(int argc, char **argv) {
	char ip[20];
	unsigned short port;
	int n;
	int i, rc, ret;

	if (argc == 1) {
		return -1;
	}

	n = 1;
	ret = 0;
	while (*++argv) {
		if (!strcmp(*argv, "-n")) {
			n = atoi(*++argv);
			if (n == 0) {
				return -1;
			}
		} else {
			rc = GetIpPort(*argv, ip, &port);
			if (rc == 0) {
				for (i = 0; i < n; i++) {
					ret = PortScan(ip, port);
					if (ret == 0) {
						break;
					}
				}
				if (ret != 0) {
					return ret;
				}
			}
		}
	}
	return 0;
}

