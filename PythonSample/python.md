# Pythonコードスニペット集
#### よく使うPythonの小技や便利コード（日付操作、リスト・辞書処理、NumPy等）をまとめたスニペット集
![](./img/pict022.png)

Pythonで開発を行う際に頻繁に使用するコードパターンやテクニックをカテゴリ別にまとめています。

### 見出し・目次

1. printで改行しない
2. 一時ファイルの利用
3. NumPy CSVファイル出力
4. 2つのリストから辞書を作成
5. 文字列結合（SQL文などの条件構築）
6. 数値のゼロ埋めフォーマット
7. NaNの除外
8. 日付・時間操作 (`datetime`)
9. 文字列から日付への変換
10. 日付から文字列への変換
11. 日付の加算・減算
12. YYYYMMDD フォーマット変換
13. 指定期間の日付リスト生成
14. 29時間制（深夜時間帯）の変換処理

#### テキストファイル
[プログラムサンプル(python).txt](./download/sample/プログラムサンプル(python).txt)

---

### 出力・ファイル操作

#### printで改行しない
`print` 関数の `end` 引数を空文字に指定することで、出力後の改行を防ぎます。

```python:print_no_newline.py
print("hello", end="")
```

#### 一時ファイルの利用
`tempfile` モジュールを使用して、一時的なファイルを作成・操作します。

```python:tempfile_example.py
import tempfile

synopsis = item.find("Synopsis").text
with tempfile.TemporaryFile(mode='w+t') as fp:
    fp.write(synopsis)
    fp.seek(0)
    synopsis = fp.read()
```

#### NumPy CSVファイル出力
NumPy配列をCSV形式で保存します。

```python:numpy_export.py
import numpy as np

np.savetxt('out.csv', predict_result, delimiter=',')
```

---

### データ構造・文字列操作

#### 2つのリストから辞書を作成
`zip` と `dict` を組み合わせて、キーと値のリストから辞書を生成します。

```python:lists_to_dict.py
keys = ['a', 'b', 'c']
values = [10, 20, 30]

dic = dict(zip(keys, values)) # => {'a': 10, 'b': 20, 'c': 30}
```

#### 文字列結合（SQL文などの条件構築）
リスト要素をカンマ区切りで結合します。

```python:join_strings.py
network_ids_str = '","'.join(self.list_network_id)
sql_condition = f"epg.network_id in ('{\"', '\"'.join(self.list_network_id)}')"
```

#### 数値のゼロ埋めフォーマット
時刻数値を `HH:MM - HH:MM` 形式の文字列に整えます。

```python:format_time_str.py
time_str = '{:02d}:{:02d} - {:02d}:{:02d}'.format(
    int(time / 100), time % 100, int(time2 / 100), time2 % 100
)
```

#### NaNの除外
リスト内包表記を使用して `np.nan` を取り除きます。

```python:remove_nan.py
import numpy as np

a_list = [x for x in a_list if x is not np.nan]
```

---

### 日付・時間操作 (`datetime`)

#### 文字列から日付への変換
文字列を `datetime` オブジェクトに変換します。

```python:str_to_date.py
import datetime

string_date = '2017/07/01 12:06:19'
dt = datetime.datetime.strptime(string_date, '%Y/%m/%d %H:%M:%S')

# date型・datetime型の取得
d = datetime.date(dt.year, dt.month, dt.day)
dt_obj = datetime.datetime(dt.year, dt.month, dt.day, dt.hour, dt.minute, dt.second)
```

#### 日付から文字列への変換
`strftime` を使用して日付オブジェクトを指定フォーマットの文字列に変換します。

```python:date_to_str.py
import datetime

now = datetime.datetime.now()
str_slash = now.strftime('%Y/%m/%d %H:%M:%S')
str_hyphen = now.strftime('%Y-%m-%d %H:%M:%S')
```

#### 日付の加算・減算
`timedelta` を使用して日付や時間の計算を行います。

```python:date_arithmetic.py
import datetime

now = datetime.datetime.now()
now + datetime.timedelta(weeks=1)    # 1週間 加算
now - datetime.timedelta(days=10)    # 10日 減算
now + datetime.timedelta(hours=10)   # 10時間 加算
now - datetime.timedelta(minutes=10) # 10分 減算
now + datetime.timedelta(seconds=10) # 10秒 加算
now + datetime.timedelta(days=7, hours=1, seconds=10) # 7日1時間10秒 加算
```

#### YYYYMMDD フォーマット変換
文字列スライスを利用して `YYYYMMDD` を `YYYY/MM/DD` に変換します。

```python:format_yyyymmdd.py
d = '20190101'
day = d[0:4] + '/' + d[4:6] + '/' + d[6:8]
```

#### 指定期間の日付リスト生成
開始日と終了日を指定して、期間内の全日付のリストを生成する関数です。

```python:get_date_list.py
import datetime

def get_date_list(start_date, end_date):
    days = []
    d0 = datetime.datetime.strptime(start_date, '%Y%m%d')
    d1 = datetime.datetime.strptime(end_date, '%Y%m%d')
    d_size = (d1 - d0).days + 1
    for i in range(d_size):
        d = d0 + datetime.timedelta(days=i)
        days.append(d.strftime('%Y%m%d'))
    return days
```

#### 29時間制（深夜時間帯）の変換処理
深夜（0時〜4時）を前日の24時以降（24時〜28時）として扱う放送業界などで使われる時間表記ロジックです。

:::message
0:00〜4:59 の時間帯を「前日の24:00〜28:59」として計算します。
:::

```python:housou_time.py
import datetime

def make_housou_day(dt):
    """日時から29時間制の日付を取得"""
    if 0 <= dt.hour <= 4:
        dt = dt + datetime.timedelta(days=-1)
    date = datetime.datetime.date(dt)
    return date

def make_housou_time(dt):
    """日時から29時間制の時間を取得"""
    plus = 0
    if 0 <= dt.hour <= 4:
        plus = 24
    return '%02d' % (dt.hour + plus) + ':' + '%02d' % (dt.minute)
```
