# usage: host port database kb
beeline -u jdbc:hive2://$1:$2/$3 << EOF
drop table $4;                        
drop table $4_basestats;
drop table $4_direct_preds;
drop table $4_dph;
drop table $4_ds;
drop table $4_dt;
drop table $4_lstr;
drop table $4_reverse_preds;
drop table $4_rph;
drop table $4_rs;
drop table $4_topkstats;
EOF