#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
人员旅行数据导入脚本（本地运行版）

用法:
    python3 tools/import_data.py -H 10.26.20.4 -P 13306 -u root -p 123456 -d citel_statistics

可选参数:
    --data data/data.txt    数据文件路径（默认 data/data.txt）
    --ignore                插入时忽略已存在的主键（INSERT IGNORE）
    --batch 500             每批插入行数

前置条件:
    1. 已执行 sql/init.sql 建库建表
    2. 本机已安装 pymysql:  python3 -m pip install pymysql
"""
import argparse
import pymysql


def parse_line(line):
    parts = line.strip().split(";")
    if len(parts) != 5:
        raise ValueError("字段数不是 5: %r" % line)
    return (int(parts[0]), int(parts[1]), int(parts[2]),
            float(parts[3]), float(parts[4]))


def main():
    ap = argparse.ArgumentParser(description="导入数据到 MySQL person 表")
    ap.add_argument("-H", "--host", default="10.26.20.4")
    ap.add_argument("-P", "--port", type=int, default=13306)
    ap.add_argument("-u", "--user", default="root")
    ap.add_argument("-p", "--password", default="123456")
    ap.add_argument("-d", "--database", default="citel_statistics")
    ap.add_argument("--data", default="data/data.txt")
    ap.add_argument("--batch", type=int, default=500)
    ap.add_argument("--ignore", action="store_true", help="使用 INSERT IGNORE 忽略已存在主键")
    args = ap.parse_args()

    rows = []
    with open(args.data, encoding="utf-8") as f:
        for line in f:
            if not line.strip():
                continue
            rows.append(parse_line(line))

    conn = pymysql.connect(host=args.host, port=args.port, user=args.user,
                           password=args.password, database=args.database,
                           connect_timeout=15)
    sql = ("INSERT INTO person (id, gender, birth_year, total_mileage, total_travel_time) "
           "VALUES (%s, %s, %s, %s, %s)")
    if args.ignore:
        sql = "INSERT IGNORE " + sql[len("INSERT "):]

    affected = 0
    with conn.cursor() as cur:
        for i in range(0, len(rows), args.batch):
            cur.executemany(sql, rows[i:i + args.batch])
            affected += cur.rowcount
    conn.commit()
    conn.close()
    print("导入完成: 共 %d 行, 影响 %d 行" % (len(rows), affected))


if __name__ == "__main__":
    main()
