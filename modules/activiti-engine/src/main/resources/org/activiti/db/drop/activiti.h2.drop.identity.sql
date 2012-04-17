alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop constraint ${prefix}ACT_FK_MEMB_GROUP;
    
alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop constraint ${prefix}ACT_FK_MEMB_USER;
    
drop table ${prefix}ACT_ID_INFO if exists;
drop table ${prefix}ACT_ID_GROUP if exists;
drop table ${prefix}ACT_ID_MEMBERSHIP if exists;
drop table ${prefix}ACT_ID_USER if exists;
