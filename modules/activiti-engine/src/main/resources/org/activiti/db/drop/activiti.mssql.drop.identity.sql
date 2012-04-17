alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop constraint ${prefix}ACT_FK_MEMB_GROUP;
    
alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop constraint ${prefix}ACT_FK_MEMB_USER;
    
if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME = '${prefix}ACT_ID_INFO') drop table ${prefix}ACT_ID_INFO;
if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME = '${prefix}ACT_ID_MEMBERSHIP') drop table ${prefix}ACT_ID_MEMBERSHIP;
if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME = '${prefix}ACT_ID_GROUP') drop table ${prefix}ACT_ID_GROUP;
if exists (select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_NAME = '${prefix}ACT_ID_USER') drop table ${prefix}ACT_ID_USER;
