# Python pandas 処理パターン・スニペット集
#### Pythonのデータ分析ライブラリ「pandas」で頻出するデータの読み込み、加工、整形、出力などの処理コードのまとめ
![](./img/pict023.png)

pandasを利用したデータ処理において、よく使われる具体的な実装コード例を機能別にまとめています。

### 見出し・目次

1. CSV読み込みとリスト抽出
2. DataFrame特定行列の値変更
3. 欠損値補完（fillna）と文字列操作
4. ループとapplyの組み合わせ
5. 列操作（削除・並び順変更・列名変更）
6. データ整形（ソート・重複処理）
7. 型変換・辞書化・データ抽出
8. データ結合・結合時の欠損補完
9. 日時処理とカスタム関数（apply）
10. JSON出力・グループ化・縦横変換

#### テキストファイル
[プログラムサンプル(pandas).txt](./download/sample/プログラムサンプル(pandas).txt)

---

### CSVファイル読み込み -> リスト抽出

```python
import pandas as pd

predict_target_class_path = 'predict_target_class.txt'
df = pd.read_csv(predict_target_class_path, comment='#')
print(df)

target_class_list = df['target_class'].values.tolist()
print(target_class_list)
```

### DataFrame特定行列の値変更

```python
df.ix[10:20, ['setai_last_year']] = None
df.ix[15:25, ['setai_counter_last_year']] = None
```

### リストの文字列変更、抜けている部分のみのfillna

```python
target_class_list_pre8 = ",".join(target_class_list_tmp).replace('_last_year', '').split(',')
pre8_values = {'%s_last_year' % target: df['pre8_%s' % target] for target in target_class_list_pre8}
add_rating_df = add_rating_df.fillna(pre8_values)
```

### ループとapplyの組み合わせ

```python
for col in col_list:
    pre8_col = 'pre8_%s' % (str(col).replace('_last_year', ''))
    df[col] = df.apply(lambda x: x[pre8_col] if x[col] == None else x[col], axis=1)
```

### 列削除・列順入れ替え・列名変更

#### 列削除

```python
df.drop('index', axis=1, inplace=True)
df.drop(['index1', 'index2'], axis=1, inplace=True)
df = df.reset_index(drop=True)
```

#### 列順入れ替え

```python
df = df.loc[:, ['a', 'c', 'b']]
```

#### 列名変更

```python
df = df.rename(columns={'A': 'a'}, index={'ONE': 'one'})
df.columns = ['broadcast_start_date', 'broadcast_end_date']
```

### ソート・重複処理

#### ソート

```python
df.sort_values(by=['network_id', 'broadcast_start_date', 'broadcast_end_date', 'version'], ascending=[True, True, True], inplace=True)
```

#### 重複削除

```python
df.drop_duplicates(subset=['state', 'point'], keep='last', inplace=True)
```

#### 重複取得

```python
dup_df = df[df.duplicated(subset=['network_id', 'broadcast_start_date', 'broadcast_end_date'], keep='last')]
```

### 抽出とデータ変換

#### リスト変換、辞書変換

```python
date_list = df.date_dt.tolist()

code_list = df.vr_bangumi_code.tolist()
name_list = df.vr_bangumi_name.tolist()
d = dict(zip(code_list, name_list))

y_list = y.values.tolist()          # 二次元配列
y_list = y.iloc[:, 0].values.tolist() # 一次元配列
```

#### 行抽出

```python
df0 = df[df.A > 0]
df1 = df0[(df0.housou_time >= df.start_time) & (df0.housou_time <= df.end_time) & (df.weekday == df0.week_day)]

df1 = df[df['state'].isin(['NY', 'TX'])]
```

#### 最終行取得

```python
last_df = df.tail(1)
value = last_df.iloc[0, 0]
value = last_df.ix[0, 'date']
```

### 結合処理・値の補完

#### 結合（merge）

```python
df = pd.merge(r_df, w_df, on='day_diff')
df = pd.merge(predict_df, work_df, on=['network_id', 'broadcast_start_date', 'broadcast_end_date', 'day_diff'])
```

#### 列の値が空の場合、他の列の値を設定する

```python
df = pd.merge(df_data, df_excel, left_on=df_data.columns[0], right_on=df_excel.columns[0], how='left')
df['新分類'] = df['新分類'].fillna(df['分類'])
```

### CSVの加工と出力

#### CSVを読み込んで列削除・並び順変更

```python
df0 = pd.read_csv('./weather_tokyo_2017.csv')
df = df0[['dt_iso', 'city_id', 'weather_id', 'weather_main']]
df = df.reset_index()
df['data_dt'] = df.dt_iso.apply(lambda x: x[:19])
df['data_dt'] = df.data_dt.apply(lambda x: datetime.datetime.strptime(x, '%Y-%m-%d %H:%M:%S'))
df.drop('dt_iso', axis=1, inplace=True)
df = df.ix[:, ['city_id', 'data_dt', 'weather_id', 'weather_main']]
```

#### CSV出力

```python
df.to_csv('data/dst/to_csv_out_header_index.csv', header=False, index=False, encoding='utf_8_sig')
```

### applyの高度な利用

#### 複数引数を取る関数の適用

```python
def make_datetime(date, time):
    itime = int(time)
    flag = False
    if itime >= 2400:
        itime = itime - 2400
        flag = True
    tstr = str(date) + ' ' + ('%04d' % itime) + '00'
    dt = datetime.datetime.strptime(tstr, '%Y%m%d %H%M%S')
    if flag == True:
        dt = dt + datetime.timedelta(days=1)
    return dt

df['start_datetime'] = df.apply(lambda x: make_datetime(x.housou_day, x.start_time), axis=1)
df['end_datetime'] = df.apply(lambda x: make_datetime(x.housou_day, x.end_time), axis=1)
```

```python
df.loc[:, 'start_housou_time'] = df[['start_housou_time', 'end_housou_time']].apply(lambda x: x[0] if x[0] <= x[1] else '0500', axis=1)
```

#### 複数戻り値を返す関数の適用

```python
def move_split_day(start_time, end_time):
    if int(start_time) > int(end_time):
        diff_a = 2900 - int(start_time)
        diff_b = int(end_time) - 500
        if diff_a > diff_b:
            end_time = '0500'
        else:
            start_time = '0500'
    return pd.Series([start_time, end_time])

if __name__ == '__main__':
    df.loc[:, ['start_housou_time', 'end_housou_time']] = df[['start_housou_time', 'end_housou_time']].apply(lambda x: move_split_day(x[0], x[1]), axis=1)

# （参考）
# df[week_col_list] = df[['housou_week']].apply(lambda x: set_week_column(x[0]), axis=1)
# df[week_col_list] = df['housou_week'].apply(lambda x: set_week_column(x))
```

```python
def get_same_start_end_time(df):
    if df['broadcast_start_date'] == df['broadcast_end_date']:
        broadcast_end_date = df['broadcast_start_date_next']
        broadcast_end_dhm = df['broadcast_start_date_next'].replace(second=0).replace(microsecond=0)
    else:
        broadcast_end_date = df['broadcast_end_date']
        broadcast_end_dhm = df['broadcast_end_dhm']
    return pd.Series([broadcast_end_date, broadcast_end_dhm])

def set_same_start_end_time(df):
    df['broadcast_start_date_next'] = df['broadcast_start_date'].shift(-1)
    df[['broadcast_end_date', 'broadcast_end_dhm']] = df.apply(lambda x: get_same_start_end_time(x), axis=1)
    df.drop('broadcast_start_date_next', axis=1, inplace=True)
    return df
```

### ループ処理とJSON化

#### 行ループ

```python
for _i, row in df.iterrows():
    print(row)
```

#### DB取得からのJSON生成

```python
def json_serial(obj):
    if isinstance(obj, (datetime, date)):
        dt_str = obj.isoformat()
        return dt_str.replace('T', ' ')
    else:
        return obj

def make_json(file_path):
    path = os.path.dirname(os.path.abspath(__file__))

    cnx = util.get_db_connect(path, area='control')
    cursor = cnx.cursor(buffered=True)
    cnx.autocommit = True

    query = """
    SELECT id,start_dt,end_dt,status,file_name FROM iclimax_control ORDER BY start_dt DESC
    """
    cursor.execute(query)

    df = pd.DataFrame(np.array(cursor.fetchall()))
    df.rename(columns={0: 'id', 1: 'start_dt', 2: 'end_dt', 3: 'status', 4: 'file_path'}, inplace=True)
    df.replace({None: ''}, inplace=True)
    df = df.T
    df.fillna('')
    data = df.to_dict()

    fw = open(file_path, 'w')
    json.dump(data, fw, indent=4, default=json_serial)
    fw.close()

    cursor.close()
    cnx.close()
```

### その他のテクニック

#### Copy警告の回避 (SettingWithCopyWarning)

```python
df = df.reset_index(drop=True)
# あるいは
df['day_diff'] = 1   # ---> df = df.assign(day_diff=1)
```

#### 縦・横変換（ピボット的な処理）

```python
import pandas as pd

df = pd.read_csv('aaa.csv', index_col=0)

arr = [[0 for i in range(4)] for j in range(7)]
for _i, row in df.iterrows():
    i = row['day_diff'] - 1
    j = row['rain_type'] - 1
    arr[i][j] = arr[i][j] + row['forcast_date']

df = pd.DataFrame()
for i in range(7):
    s = pd.Series({
        'day_diff': i + 1,
        'type1': arr[i][0],
        'type2': arr[i][1],
        'type3': arr[i][2],
        'type4': arr[i][3],
    })
    df = df.append(s, ignore_index=True)
df = df.astype('i1')
print(df)
```

| no | day_diff | rain_type | forcast_date |
|---:|---------:|----------:|-------------:|
| 0  |        1 |         1 |           22 |
| 1  |        1 |         2 |            1 |
| 2  |        1 |         3 |            1 |
| 3  |        1 |         4 |            7 |
| 4  |        2 |         1 |           23 |
| 5  |        2 |         3 |            2 |
| 6  |        2 |         4 |            6 |
| 7  |        3 |         1 |           23 |
| 8  |        3 |         2 |            2 |
| 9  |        3 |         3 |            2 |
| 10 |        3 |         4 |            4 |
| 11 |        4 |         1 |           20 |
| 12 |        4 |         2 |            6 |
| 13 |        4 |         3 |            1 |
| 14 |        4 |         4 |            4 |
| 15 |        5 |         1 |           18 |
| 16 |        5 |         2 |            8 |
| 17 |        5 |         3 |            3 |
| 18 |        5 |         4 |            2 |
| 19 |        6 |         1 |           18 |
| 20 |        6 |         2 |            7 |
| 21 |        6 |         3 |            5 |
| 22 |        6 |         4 |            1 |
| 23 |        7 |         1 |           17 |
| 24 |        7 |         2 |            7 |
| 25 |        7 |         3 |            6 |
| 26 |        7 |         4 |            1 |
                              
| no | day_diff | type1 | type2 | type3 | type4 |
|---:|---------:|------:|------:|------:|------:|
|  0 |        1 |    22 |     1 |     1 |      7|
|  1 |        2 |    23 |     0 |     2 |      6|
|  2 |        3 |    23 |     2 |     2 |      4|
|  3 |        4 |    20 |     6 |     1 |      4|
|  4 |        5 |    18 |     8 |     3 |      2|
|  5 |        6 |    18 |     7 |     5 |      1|
|  6 |        7 |    17 |     7 |     6 |      1|

#### グループ化関連

```python
# グループごとに連番を振る
df['count'] = df.groupby('ID').cumcount() + 1

# グループ内容の表示
gp = df.groupby(['program_id', 'epg_split_id'])
print(gp[self.target_class_list].groups)

# グループ単位にNaNの直前の値でNaNを補完する
df.groupby('key').ffill()
```

#### 値のシフト・NaN処理・最終行セット

```python
# 1列（行）ずらす
df['end_dt'] = df['data_dt'].shift(-1)

# NaNの補完（代入しないと反映されない）
df[target] = df[target].fillna(0)

# NaNの判定
for i, row in df2.iterrows():
    if pd.isnull(df2.loc[i, 'version']):
        print(row['media_id'], row['start_datetime'], row['bangumi_name'])

# 最終行のみ値をセット
df.iloc[-1, :] = value
df.iloc[-1, df.columns.get_loc(target_end)] = value

# 型指定
df = df.astype({'code': 'int64', 'value': 'float64'})
```

#### 行分割と再結合

```python
predict_df = pd.read_csv('predict_df.csv')
size = len(predict_df.index)

predict_df_list = []
for i in range(0, size, 100):
    predict_df_list.append(predict_df[i:i + 100])

result_df = pd.DataFrame()
for df in predict_df_list:
    result_df = result_df.append(df)
```

---

### パフォーマンス最適化

#### swifterによるPandas applyの高速化
Pandasの `apply` メソッドの前に `swifter` を挟むことで処理を自動的に並列化・高速化します。

```python:swifter_apply.py
# applyメソッドの前にswifterを加える
%time df['col2'] = df['col'].swifter.apply(lambda x: x**2)
```