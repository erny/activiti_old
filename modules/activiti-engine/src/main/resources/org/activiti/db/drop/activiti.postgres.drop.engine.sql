drop index ${prefix}ACT_IDX_BYTEAR_DEPL ;
drop index ${prefix}ACT_IDX_EXE_PROCINST ;
drop index ${prefix}ACT_IDX_EXE_PARENT ;
drop index ${prefix}ACT_IDX_EXE_SUPER;
drop index ${prefix}ACT_IDX_MEMB_GROUP;
drop index ${prefix}ACT_IDX_MEMB_USER;
drop index ${prefix}ACT_IDX_TSKASS_TASK;
drop index ${prefix}ACT_IDX_TASK_EXEC;
drop index ${prefix}ACT_IDX_TASK_PROCINST;
drop index ${prefix}ACT_IDX_TASK_PROCDEF;
drop index ${prefix}ACT_IDX_VAR_EXE;
drop index ${prefix}ACT_IDX_VAR_PROCINST;
drop index ${prefix}ACT_IDX_VAR_BYTEARRAY;
drop index ${prefix}ACT_IDX_JOB_EXCEPTION;

drop index ${prefix}ACT_IDX_EXEC_BUSKEY;
drop index ${prefix}ACT_IDX_TASK_CREATE;
drop index ${prefix}ACT_IDX_IDENT_LNK_USER;
drop index ${prefix}ACT_IDX_IDENT_LNK_GROUP;
drop index ${prefix}ACT_IDX_EVENT_SUBSCR;


alter table ${prefix}ACT_GE_BYTEARRAY 
    drop constraint ${prefix}ACT_FK_BYTEARR_DEPL;

alter table ${prefix}ACT_RU_EXECUTION
    drop constraint ${prefix}ACT_FK_EXE_PROCINST;

alter table ${prefix}ACT_RU_EXECUTION 
    drop constraint ${prefix}ACT_FK_EXE_PARENT;

alter table ${prefix}ACT_RU_EXECUTION 
    drop constraint ${prefix}ACT_FK_EXE_SUPER;
    
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
    
drop table ${prefix}ACT_GE_PROPERTY;
drop table ${prefix}ACT_GE_BYTEARRAY;
drop table ${prefix}ACT_RE_DEPLOYMENT;
drop table ${prefix}ACT_RE_PROCDEF;
drop table ${prefix}ACT_RU_EXECUTION;
drop table ${prefix}ACT_RU_JOB;
drop table ${prefix}ACT_RU_TASK;
drop table ${prefix}ACT_RU_IDENTITYLINK;
drop table ${prefix}ACT_RU_VARIABLE;
drop table ${prefix}ACT_RU_EVENT_SUBSCR;
