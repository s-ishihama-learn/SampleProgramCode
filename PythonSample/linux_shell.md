# Bash シェルスクリプト・コマンドパターン集
#### Linux/Bashで頻出する日付ループ、ファイル処理、定期的コマンド実行などのスクリプトサンプルまとめ
![](./img/pict021.png)

Bashシェルスクリプトやコマンドラインでの定常作業でよく使われる便利な処理パターンをまとめています。

### 見出し・目次

1. 日付ループ処理
2. 年月・日付ファイル出力
3. コマンドの定期・連続実行
4. ファイル一覧ループと解凍処理
5. ファイルを1行ずつ読み込んで処理
6. ディレクトリ内のファイル数確認

#### テキストファイル
[プログラムサンプル(LinuxShell).txt](./download/sample/プログラムサンプル(LinuxShell).txt)

---

### 日付ループ処理

#### YYYYMMDD 形式の日付ループ

```bash
#!/bin/bash

STARTDATE=20180301
ENDDATE=20181004

CURRENTDATE=$STARTDATE
while [ 1 ] ; do
    # 処理
    if [ $CURRENTDATE =$ENDDATE ] ; then
        break
    fi
    python predict_p_kanto_weather.py $CURRENTDATE
    CURRENTDATE=`date -d "$CURRENTDATE 1day" "+%Y%m%d"`
done
```

#### YYYY-MM-DD 形式（ハイフン付き）の日付ループ

```bash
#!/bin/bash

STARTDATE=2018-03-01
ENDDATE=2018-10-04

CURRENTDATE=$STARTDATE
while [ 1 ] ; do
    # 処理
    if [ $CURRENTDATE =$ENDDATE ] ; then
        break
    fi
    python predict_p_kanto_weather.py $CURRENTDATE
    CURRENTDATE=`date -d "$CURRENTDATE 1day" "+%Y-%m-%d"`
done
```

### 年月・日付ファイル出力

#### 当月ログファイルへの出力

```bash
dm=`date "+%Y%m"`
perl 10epg.pl >>logs/10epg_$dm.log 2>&1
```

#### 本日日付のファイル書き出し

```bash
date +%F > today.txt
# today.txt ---> 2018-02-23
```

### コマンドの定期・連続実行

:::message
`watch` コマンドを使用することで指定秒数ごとに定期的にコマンドを実行・監視できます。
:::

```bash
# 1秒ごとにファイル一覧を表示
watch -n 1 ls -la

# 60秒ごとにPythonプロセスを監視
watch -n 60 ps aux | grep python
```

### ファイル一覧ループと解凍処理

```bash
for file in `\find . -name '*.tar'`; do
    tar xvf $file
done
```

### ファイルを1行ずつ読み込んで処理

```bash
#!/bin/bash

while read line
do
  fname=`echo ${line} | awk -F "/" '{ print $NF }'`
  `gsutil cp ${line} ${fname}`
  `gsutil cp ${fname} gs://interface-file-past/using_data/epg_pv/${fname}`
  `rm -f ${fname}`
done < epg_pv.list
```

### ディレクトリ内のファイル数確認

```bash
#!/bin/bash

dir_list=`ls -R ~/Backend_Py3/rt_model/model/ | grep "/1:"`
for file in $dir_list; do
	dir=`echo $file | sed -e 's/1://g'`
	if [ `echo $dir | grep c7` ]; then
		size=`ls -la ${dir}1 | wc -l`
		echo $size:$dir${i}
	else
		for i in {1..7} ; do
			size=`ls -la $dir${i} | wc -l`
			echo $size:$dir${i}
		done
	fi
done

dir_list=`ls -R ~/Backend_Py3/zero_model/model/ | grep -E "/[0-9]{8}:"`
for file in $dir_list; do
	dir=`echo $file | sed -e 's/://g'`
	size=`ls -la ${dir} | wc -l`
	echo $size:$dir
done
```
