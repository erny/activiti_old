drop index ${prefix}ACT_IDX_EXEC_BUSKEY;
drop index ${prefix}ACT_IDX_TASK_CREATE;
drop index ${prefix}ACT_IDX_IDENT_LNK_USER;
drop index ${prefix}ACT_IDX_IDENT_LNK_GROUP;

alter table ${prefix}ACT_GE_BYTEARRAY 
    drop constraint ${prefix}ACT_FK_BYTEARR_DEPL;

alter table ${prefix}ACT_RU_EXECUTION
    drop constraint ${prefix}ACT_FK_EXE_PROCINST;

alter table ${prefix}ACT_RU_EXECUTION 
    drop constraint ${prefix}ACT_FK_EXE_PARENT;

alter table ${prefix}ACT_RU_EXECUTION 
    drop constraint ${prefix}ACT_FK_EXE_SUPER;
    
alter table ${prefix}ACT_RU_EXECUTION
    drop constraint ${prefix}ACT_UNIQ_RU_BUS_KEY;
    
alter table ${prefix}ACT_RU_IDENTITYLINK
    drop constraint ${prefix}ACT_FK_TSKASS_TASK;
 
alter table ${prefix}ACT_RU_TASK
	drop constraint ${prefix}ACT_FK_TASK_EXE;

alter table ${prefix}ACT_RU_TASK
	drop constraint ${prefix}ACT_FK_TASK_PROCINST;
	
alter table ${prefix}ACT_RU_TASK
	drop constraint ${prefix}ACT_FK_TASK_PROCDEF;
	
alter table ${prefix}ACT_RU_VARIABLE
    drop constraint ${prefix}ACT_FK_VAR_EXE;
    
alter table ${prefix}ACT_RU_VARIABLE
    drop constraint ${prefix}ACT_FK_VAR_PROCINST;
    
alter table ${prefix}ACT_RU_VARIABLE
    drop constraint ${prefix}ACT_FK_VAR_BYTEARRAY;

alter table ${prefix}ACT_RU_JOB
    drop constraint ${prefix}ACT_FK_JOB_EXCEPTION;
    
alter table ${prefix}ACT_RU_EVENT_SUBSCR
    drop constraint ${prefix}ACT_FK_EVENT_EXEC;    
    
drop table ${prefix}ACT_GE_PROPERTY if exists;
drop table ${prefix}ACT_GE_BYTEARRAY if exists;
drop table ${prefix}ACT_RE_DEPLOYMENT if exists;
drop table ${prefix}ACT_RU_EXECUTION if exists;
drop table ${prefix}ACT_RU_JOB if exists;
drop table ${prefix}ACT_RE_PROCDEF if exists;
drop table ${prefix}ACT_RU_TASK if exists;
drop table ${prefix}ACT_RU_IDENTITYLINK if exists;
drop table ${prefix}ACT_RU_VARIABLE if exists;
drop table ${prefix}ACT_RU_EVENT_SUBSCR if exists;
