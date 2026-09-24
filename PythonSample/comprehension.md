# Python リスト操作・内包表記サンプル集
#### Pythonにおけるリスト操作や内包表記（二次元リストの一次元化、条件分岐、辞書生成、コードの簡略化など）のサンプルコード集
![](./img/pict024.png)

Pythonのリスト操作および内包表記を活用した実践的なコードパターンをまとめています。

### 見出し・目次

1. 二次元リストの一次元化
2. リストの一部削除
3. 内包表記の実践例
4. 内包表記によるコードの書き換え例

#### テキストファイル
[プログラムサンプル(内包表記).txt](./download/sample/プログラムサンプル(内包表記).txt)

---

### 二次元リストの一次元化

```python
target_list = ['setai', 'kozin', 'child0412', 'child1319', 'F1', 'F2', 'F3', 'M1', 'M2', 'M3']
target_c_list = [[s + '_corr', s + '_rmse'] for s in target_list]
target_x_list = ['month'] + [x for y in target_c_list for x in y]
print(target_x_list)
```

### リストの一部削除

```python
target_x_list.remove('month')
```

### 内包表記の実践例

#### 時間数値リストから `datetime.time` オブジェクトへの変換

```python
import datetime

time_list_int = [515, 530, 545, 600, 615, 630, 645, 700, 715, 730, 745, 800]

# 段階的に処理する場合
l1 = [[int(int(t) / 100), int(t) % 100] for t in time_list_int]
l2 = [datetime.time(hour=h, minute=m, second=0, microsecond=0) for h, m in l1]
print(l2)
# 出力: [datetime.time(5, 15), datetime.time(5, 30), datetime.time(5, 45), datetime.time(6, 0), datetime.time(6, 15), datetime.time(6, 30), datetime.time(6, 45), datetime.time(7, 0), datetime.time(7, 15), datetime.time(7, 30), datetime.time(7, 45), datetime.time(8, 0)]

# 1行で記述する場合
time_list = [datetime.time(hour=h, minute=m, second=0, microsecond=0) for h, m in [[int(int(t) / 100), int(t) % 100] for t in time_list_int]]
print(time_list)
# 出力: [datetime.time(5, 15), datetime.time(5, 30), datetime.time(5, 45), datetime.time(6, 0), datetime.time(6, 15), datetime.time(6, 30), datetime.time(6, 45), datetime.time(7, 0), datetime.time(7, 15), datetime.time(7, 30), datetime.time(7, 45), datetime.time(8, 0)]
```

#### SQL文字列の生成

```python
target_list = ['setai', 'kozin', 'child0412', 'child1319', 'F1', 'F2', 'F3', 'M1', 'M2', 'M3']
sum_str_list = ['SUM(' + s + ') AS ' + s for s in target_list]
```

#### 三項演算子を使った値の置き換え

```python
target_list = ['setai', 'kozin', 'child', 'teen', 'F1', 'F2', 'F3', 'M1', 'M2', 'M3']

# 'child' -> 'child0412', 'teen' -> 'child1319' に置換
convert_list = ['child0412' if s == 'child' else 'child1319' if s == 'teen' else s for s in target_list]

# 逆の置換処理
convert_list = ['child' if s == 'child0412' else 'teen' if s == 'child1319' else s for s in target_list]
```

#### 2つのリストを使って辞書を作成

```python
all_target_list = ['setai', 'kozin', 'child0412', 'child1319', 'MF20plus', 'M1', 'M2', 'M3', 'F20plus', 'F1', 'F2', 'F3', 'shufu', 'F5059', 'shufukid', 'F1834', 'F2539', 'M2049', 'fp845', 'fp846']
p_all_target_list = [f'p_{target}' for target in all_target_list]

# zipと辞書内包表記の利用
target_name_dic = {name: p_name for name, p_name in zip(all_target_list, p_all_target_list)}
```

---

### 内包表記（書き換え例）

#### 例1: 判定処理の書き換え

:::message
`all()` とジェネレータ式を組み合わせることで、forループを使わずに簡潔に記述できます。
:::

**変更前 (forループ)**

```python
judge = True
for i in range(len(d)):
    if np.abs(d[i]) > EPS:
        judge = False
return judge
```

**変更後 (all関数 + 内包表記)**

```python
return all(np.abs(x) <= EPS for x in d)
```

#### 例2: 配列同士の加算

:::message
`zip()` とリスト内包表記を組み合わせることで、インデックス指定なしで2つのリスト要素同士を加算できます。
:::

**変更前 (forループ)**

```python
for i in range(len(c)):
    c[i] += mc[i]
```

**変更後 (zip + リスト内包表記)**

```python
c = [x + y for x, y in zip(c, mc)]
```
