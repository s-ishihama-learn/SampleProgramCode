# Port Scanner Tools

このリポジトリには、C言語およびJavaで実装された各種TCPポートスキャナープログラムが含まれています。
OS（Linux/Solaris/Windows）に応じたソケット処理の実装例や、ノンブロッキングI/Oを利用したタイムアウト処理の実装が含まれています。

---

## 📁 ファイル構成と概要

| ファイル名 | 言語 | 役割・概要 |
| :--- | :--- | :--- |
| `PortScan.c` | C | **メインプログラム**<br>コマンドライン引数の解析、IPアドレス・ポート番号の検証、およびスキャン処理の呼び出しを行います。 |
| `PortScanLinux.c` | C | **Linux/Solaris向け実装 (ブロッキング)**<br>標準的な `connect()` を使用したシンプルなポート判定ロジックです。 |
| `PortScanLinuxTimeout.c` | C | **Linux/Solaris向け実装 (ノンブロッキング・タイムアウト対応)**<br>`fcntl()` と `select()` を用い、タイムアウト付きでポート開放状態を確認します。 |
| `PortScanWin.c` | C | **Windows向け実装**<br>Winsock API (`WSAStartup`, `closesocket`等) を使用したポート判定ロジックです。 |
| `testPortscan.c` | C | **テスト用メインプログラム**<br>シンプルにポートチェックを実行・検証するための簡易版メイン処理です。 |
| `PortScan.java` | Java | **Java版単体ポートスキャナー**<br>Javaの `Socket` オブジェクトを使用したシンプルでクロスプラットフォームなポートチェックツールです。 |

---

## 🚀 ビルドと実行方法 (C言語)

`PortScan.c` （メイン）と、対象のプラットフォーム向けの実装ファイルを組み合わせてビルドします。

### 1. Linux / Unix
#### 標準実装でビルド
```bash
gcc PortScan.c PortScanLinux.c -o portscan
```

#### タイムアウト対応実装でビルド
```bash
gcc PortScan.c PortScanLinuxTimeout.c -o portscan
```

### 2. Solaris
```bash
gcc -lsocket -lnsl PortScan.c PortScanLinux.c -o portscan
```

### 3. Windows (MinGW / GCC)
```bash
gcc PortScan.c PortScanWin.c -lws2_32 -o portscan.exe
```

---

## 💻 実行コマンドとオプション (C言語)

### コマンド構文
```bash
./portscan [-n 試行回数] [-t タイムアウト秒] [-o] <IPアドレス:ポート番号> ...
```

### オプション
* `-n <数値>` : 接続試行回数を指定します（デフォルト: 1）。
* `-t <数値>` : タイムアウトまでの時間（秒）を指定します（1〜30秒、デフォルト: 3）。
* `-o` : スキャン結果を標準出力（コンソール）へ出力します。

### 実行例
```bash
# 192.168.1.1 の 80番ポートを1回スキャンし、結果を表示
./portscan -o 192.168.1.1:80

# タイムアウト5秒、最大3回試行で複数ポートをスキャン
./portscan -t 5 -n 3 -o 192.168.1.10:22 192.168.1.10:80 192.168.1.10:443
```

---

## ☕ 実行方法 (Java)

Java版は単体で完結して動作します。

### コンパイル
```bash
javac PortScan.java
```

### 実行
```bash
# デフォルト (localhost:80) で実行
java PortScan

# ホストとポートを指定して実行
java PortScan 192.168.1.1 8080
```

---

## ⚠️ 免責事項 / Caution
本ツールは、自らが管理するネットワーク・システムまたは明示的に許可を得た環境に対する確認・診断目的で利用してください。許可を得ていない第三者のシステムに対してポートスキャンを実施する行為は、セキュリティポリシー違反およびサイバー攻撃とみなされる可能性があります。