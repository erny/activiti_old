alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop CONSTRAINT ${prefix}ACT_FK_MEMB_GROUP;
    
alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop CONSTRAINT ${prefix}ACT_FK_MEMB_USER;

drop index ${prefix}ACT_IDX_MEMB_GROUP;
drop index ${prefix}ACT_IDX_MEMB_USER;

drop table  ${prefix}ACT_ID_INFO;
drop table  ${prefix}ACT_ID_MEMBERSHIP;
drop table  ${prefix}ACT_ID_GROUP;
drop table  ${prefix}ACT_ID_USER;
