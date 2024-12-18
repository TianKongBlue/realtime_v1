Flink的部署模式

   stand load model 

   session

   per-Tab

   application

mapjoin的应用

```
with t1 as (
    select userid,
           order_no,
           concat(region,'-',rand()) as region,
           product_no,
           color_no,
           sale_amount,
           ts
    from date_east
),
    t2 as (
        select region,
               count(1) as cnt
        from t1
        group by region
    ),
    t3 as (
        select region,
               substr(region,1,2) as re,
               cnt
        from t2
    ),
    t4 as (
        select re,
               count(1) as cnt
        from t3
        group by re
    )
select /*+ mapjoin(dim_region_east) */
       a.*,
       b.region_id
from t4 as a
join dim_region_east as b
on a.re = b.region_name;
```

