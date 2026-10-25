package main

import (
	"log"
	"os"
)

// go build -o serverCheck.exe
// serverCheck 10.80.16.80 22 eris2tooladmin ennet5733ict /home/eris2tooladmin/ftp_test
// echo %errorlevel%

// エラーレベル
// 0: 正常
// 1: サーバのIPアドレスまたはホスト名が不正
// 2: ポート番号が不正
// 3: ユーザIDまたはパスワードが不正
// 4: （SSHセッションの確立失敗）
// 5: ディレクトリが存在しない

func main() {
	var check bool
	var ret int

	if len(os.Args) != 6 {
		log.Fatal("usage: serverCheck host_name port_number user_id password directory_path")
	}

	host := os.Args[1]
	port := os.Args[2]
	userId := os.Args[3]
	passwd := os.Args[4]
	path := os.Args[5]

	// pingコマンド
	check = serverPing(host, 1)
	if !check {
		os.Exit(1)
	}

	// ポートスキャン
	check = portScan(host, port)
	if !check {
		os.Exit(2)
	}

	// SSH接続
	ret = serverSsh(host, port, userId, passwd, path)
	if ret != 0 {
		os.Exit(ret + 2)
	}
	os.Exit(0)
}
