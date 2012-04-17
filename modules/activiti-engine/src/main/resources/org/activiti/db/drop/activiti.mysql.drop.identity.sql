alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop FOREIGN KEY ${prefix}ACT_FK_MEMB_GROUP;
    
alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop FOREIGN KEY ${prefix}ACT_FK_MEMB_USER;

drop table if exists ${prefix}ACT_ID_INFO;
drop table if exists ${prefix}ACT_ID_MEMBERSHIP;
drop table if exists ${prefix}ACT_ID_GROUP;
drop table if exists ${prefix}ACT_ID_USER;
