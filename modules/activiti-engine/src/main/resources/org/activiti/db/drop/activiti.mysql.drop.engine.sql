drop index ${prefix}ACT_IDX_EXEC_BUSKEY on ${prefix}ACT_RU_EXECUTION;
drop index ${prefix}ACT_IDX_TASK_CREATE on ${prefix}ACT_RU_TASK;
drop index ${prefix}ACT_IDX_IDENT_LNK_USER on ${prefix}ACT_RU_IDENTITYLINK;
drop index ${prefix}ACT_IDX_IDENT_LNK_GROUP on ${prefix}ACT_RU_IDENTITYLINK;

alter table ${prefix}ACT_GE_BYTEARRAY 
    drop FOREIGN KEY ${prefix}ACT_FK_BYTEARR_DEPL;

alter table ${prefix}ACT_RU_EXECUTION
    drop FOREIGN KEY ${prefix}ACT_FK_EXE_PROCINST;

alter table ${prefix}ACT_RU_EXECUTION 
    drop FOREIGN KEY ${prefix}ACT_FK_EXE_PARENT;

alter table ${prefix}ACT_RU_EXECUTION 
    drop FOREIGN KEY ${prefix}ACT_FK_EXE_SUPER;
    
alter table ${prefix}ACT_RU_IDENTITYLINK
    drop FOREIGN KEY ${prefix}ACT_FK_TSKASS_TASK;

alter table ${prefix}ACT_RU_TASK
	drop FOREIGN KEY ${prefix}ACT_FK_TASK_EXE;

alter table ${prefix}ACT_RU_TASK
	drop FOREIGN KEY ${prefix}ACT_FK_TASK_PROCINST;
	
alter table ${prefix}ACT_RU_TASK
	drop FOREIGN KEY ${prefix}ACT_FK_TASK_PROCDEF;
    
alter table ${prefix}ACT_RU_VARIABLE
    drop FOREIGN KEY ${prefix}ACT_FK_VAR_EXE;
    
alter table ${prefix}ACT_RU_VARIABLE
	drop FOREIGN KEY ${prefix}ACT_FK_VAR_PROCINST;    

alter table ${prefix}ACT_RU_VARIABLE
    drop FOREIGN KEY ${prefix}ACT_FK_VAR_BYTEARRAY;

alter table ${prefix}ACT_RU_JOB
    drop FOREIGN KEY ${prefix}ACT_FK_JOB_EXCEPTION;
    
alter table ${prefix}ACT_RU_EVENT_SUBSCR
    drop FOREIGN KEY ${prefix}ACT_FK_EVENT_EXEC; 
    
drop table if exists ${prefix}ACT_GE_PROPERTY;
drop table if exists ${prefix}ACT_RU_VARIABLE;
drop table if exists ${prefix}ACT_GE_BYTEARRAY;
drop table if exists ${prefix}ACT_RE_DEPLOYMENT;
drop table if exists ${prefix}ACT_RU_IDENTITYLINK;
drop table if exists ${prefix}ACT_RU_TASK;
drop table if exists ${prefix}ACT_RE_PROCDEF;
drop table if exists ${prefix}ACT_RU_EXECUTION;
drop table if exists ${prefix}ACT_RU_JOB; 
drop table if exists ${prefix}ACT_RU_EVENT_SUBSCR;