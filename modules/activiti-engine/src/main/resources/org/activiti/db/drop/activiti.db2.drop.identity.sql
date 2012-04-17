alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop foreign key ${prefix}ACT_FK_MEMB_GROUP;
    
alter table ${prefix}ACT_ID_MEMBERSHIP 
    drop foreign key ${prefix}ACT_FK_MEMB_USER;
    
drop table ${prefix}ACT_ID_INFO;
drop table ${prefix}ACT_ID_MEMBERSHIP;
drop table ${prefix}ACT_ID_GROUP;
drop table ${prefix}ACT_ID_USER;
